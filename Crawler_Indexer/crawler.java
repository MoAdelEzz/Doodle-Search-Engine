import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class crawler implements Runnable {

     Mongod mongo;
    double SEED_PRIORITY = 100;
    double ratio = 0.6;

    crawler(Mongod m) {
        mongo = m;
    }

    private final ArrayList<Thread> threads_array = new ArrayList<>();
    private final ArrayList<String> seed = new ArrayList<>(
            Arrays.asList(
                    "https://twitter.com/animetv_jp/status/1611734963165995009"
                    /*
                                  "https://www.facebook.com/",
                    "https://www.youtube.com/",
                     */

            ));


    public String handle_dot_slash(String parent_url, String url)
    {
        if (url.length() > 3 && url.substring(0,3).equals("../"))
        {
            int idx = parent_url.length() - 1;
            while (idx >= 0 && parent_url.charAt(idx) != '/') idx--;
            parent_url = parent_url.substring(0,idx);
        }
        int bidx = -1;

        if (url.contains("/"))
        {
            bidx = 0;
            while (bidx < url.length() && url.charAt(bidx) != '/') bidx++;
        }

        url = url.substring(bidx + 1);
        return parent_url.concat(url);
    }


    public void run() {
        try {
            get_links_of_page();
        } catch (IOException | NoSuchAlgorithmException e) {

        }
    }

    public  void main(int _of_Threads){
        try {
            initialize_i_j();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        initialize_and_run_threads(_of_Threads);
    }

     void get_links_of_page() throws IOException, NoSuchAlgorithmException {
        while (mongo.j < 6000) {

            double curr_priority;

            String current_url;
            Document currDoc;
            synchronized (mongo.lock) {
                current_url = mongo.get_next_document_to_visit();
                mongo.update_crawler_visited(current_url);
                curr_priority = mongo.get_priority(current_url);
            }

            if(current_url == null || current_url.isEmpty())
                continue;

            try {
                currDoc = Jsoup.connect(current_url).timeout(3000).get();
                // process the page contents
            } catch (IOException e) {
                continue;
            }

            System.out.println("Started crawling => " + current_url + ", page priority = " + current_url);

            ArrayList<String> robotTxt= disallowed_urls_robot_txt(current_url);
            Elements links = currDoc.select("a[href]");

            for (Element link : links) {

                String link_url = link.attr("href");

                if (!link_url.startsWith("https")) { // check if sublink
                    link_url = handle_dot_slash(current_url,link_url);
                }

                if(link_url.charAt(link_url.length() - 1) == '/')
                {
                    StringBuilder sb = new StringBuilder(link_url);
                    sb.deleteCharAt(link_url.length() - 1);
                    link_url = sb.toString();
                }

                /*
                if (!isValidUrl(link_url)) // check invalid url
                    continue;
                */
                if(robotTxt != null && robotTxt.contains(link_url)) {
                    System.out.println("ERROR ROBOT.TXT");
                    continue;
                }

                Document linkDoc;

                try {
                    linkDoc = Jsoup.connect(link_url).timeout(3000).get();
                    // process the page contents
                } catch (IOException e) {
                    continue;
                }

                String linkText = linkDoc.body().text();
                String encrypted_text = encryptText(linkText);

                synchronized (mongo.lock) {
                    boolean noSuchUid = mongo.check_repeated_uid(encrypted_text);
                    System.out.println("checking url => " + link_url + ", result = " + noSuchUid + ", uid => " + encrypted_text);
                    //boolean b2 = mongo.check_repeated_url(link_url);
                    if (noSuchUid)
                    {
                        mongo.j++;
                        mongo.insert_into_db("urls", mongo.make_crawler_document(link_url,encrypted_text, curr_priority * ratio, mongo.j));
                        System.out.println("\n currLink: " + current_url + " --> subLink: " + link_url + "  j:" + mongo.j);
                    }
                    else
                    {
                        mongo.updatePriority(curr_priority, link_url);
                    }
                }
            }
        }
    }

     public String encryptText(String message) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(message.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    void add_seed_to_database() throws NoSuchAlgorithmException {
        for (int k = 0; k < seed.size(); k++) {
            String current_url = seed.get(k);
            Document doc;
            try {
                doc = Jsoup.connect(current_url).timeout(3000).get();
            }
            catch (Exception E)
            {
                continue;
            }
            String S = doc.body().text();

            mongo.insert_into_db("urls", mongo.make_crawler_document(current_url, encryptText(S), SEED_PRIORITY,k + 1));
        }
        System.out.println("SEED FINISHED");
    }

     void initialize_i_j() throws NoSuchAlgorithmException {
        if (mongo.get_previous_urls_count() == 0) {
            mongo.j = seed.size();

            add_seed_to_database();
        } else {
            mongo.j = mongo.get_previous_urls_count();
        }
    }

     void initialize_and_run_threads(int _of_threads) {

        for (int i = 0; i < _of_threads; i++) {
            threads_array.add(new Thread(new crawler(mongo)));
        }

        // Start each thread in the array
        for (Thread thread : threads_array) {
            thread.start();
        }

        // Join each thread in the array
        for (Thread thread : threads_array) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

     boolean isValidUrl(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        }
        catch (IOException e) {
            return false;
        }
    }

     ArrayList<String> disallowed_urls_robot_txt(String baseUrl) throws IOException {

        String robotTxtUrl = baseUrl + "/robots.txt";

        if(!isValidUrl(robotTxtUrl))
            return null;

        ArrayList<String> disallowedPaths = new ArrayList<>();
        URL url = new URL(robotTxtUrl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;


        boolean inUserAgent = false;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("User-agent:")) {
                inUserAgent = line.contains("*") || line.contains("myUserAgentName");
            } else if (inUserAgent && line.startsWith("Disallow:")) {
                String disallowedPath = line.substring("Disallow:".length()).trim();
                disallowedPaths.add(baseUrl + disallowedPath);
            }
        }
        reader.close();
        return disallowedPaths;
    }
}


