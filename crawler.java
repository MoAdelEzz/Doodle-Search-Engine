import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class crawler implements Runnable{
    private int i, j;

    Mongod mongo;
    final Object synco = new Object();
    private  ArrayList<Thread> threads_array = new ArrayList<>();

    crawler(Mongod m)
    {
        mongo = m;
    }
    private final ArrayList<String> seed = new ArrayList<>(
            Arrays.asList(
                    //"https://en.wikipedia.org/", // this is shitty errors website
                    "https://www.reddit.com",
                    "https://www.bbc.com/news",
                    "https://www.youtube.com",
                    "https://github.com",
                    "https://medium.com",
                    "https://stackoverflow.com",
                    "https://www.amazon.com",
                    "https://www.quora.com",
                    "https://www.yelp.com"
            ));
    public void run()
    {
        try {
            get_links_of_page();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void main(String[] args) throws IOException {
        initialize_i_j();
        initialize_and_run_threads();
    }

    public void get_links_of_page() throws IOException {
        while (j < 5000) {
            String current_url = null;
            Document doc;
            int in_i;
            synchronized (synco){
                current_url = mongo.get_next_document_to_visit();
                i++;
                in_i = i;
                mongo.update_crawler_visited(current_url);
            }
            // added by moa
            if (current_url == null)
                return;

            System.out.println(current_url);

            try {
                doc = Jsoup.connect(current_url).get();
            }
            catch (Exception e)
            {
                continue;
            }


            String title = doc.title();
            System.out.println("title: " + title);

            Elements links = doc.select("a[href]");
            for (Element link : links) {

                String link_url = link.attr("href");

                if (!link_url.contains("https")) {
                    link_url = current_url + link_url;
                }

                if(!isValidUrl(link_url))
                    continue;

                Document linkDoc;
                try {
                    linkDoc = Jsoup.connect(link_url).timeout(3000).get();
                } catch (Exception e)
                {
                    // connection failed skip this link ~~~~~> Moa
                    continue;
                }

                String linkTitle = linkDoc.title();

                System.out.println(current_url + " => " + link_url);

                //System.out.println("title: " + linkTitle);
                String linkText = linkDoc.text();

                boolean b1 = mongo.check_repeated_uid(linkText);
                boolean b2 = mongo.check_repeated_url(link_url);

                if (b1 || b2)
                    mongo.add_url1_to_url2List(current_url, link_url);
                else {
                    synchronized (synco) {
                        mongo.insert_into_db("urls", mongo.make_crawler_document(link_url, linkText, j));
                        System.out.println("\nlink :" + link_url + "  j:" + j++ + " i:" + in_i);
                    }
                    mongo.add_url1_to_url2List(current_url, link_url);
                }
            }
        }
    }

    void add_seed_to_database() throws IOException {
        for (int k = 0; k < seed.size(); k++) {
            String current_url = seed.get(k);
            Document doc = Jsoup.connect(current_url).get();
            String S = doc.text();

            mongo.insert_into_db("urls", mongo.make_crawler_document(current_url, S, k + 1));
        }
        System.out.println("SEED FINISHED");
    }

    boolean isValidUrl(String url) throws MalformedURLException {
        try {
            // it will check only for scheme and not null input
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    void initialize_i_j() throws IOException {
        if (mongo.get_previous_urls().isEmpty()) {
            i = 0;
            j = seed.size();

            add_seed_to_database();
        } else {
            i = mongo.get_index_to_continou_crawling();
            j = mongo.get_previous_urls().size();
        }
    }

    void initialize_and_run_threads()
    {

        for (int i = 0; i < 10; i++) {
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

}


// take one link from  the database
//