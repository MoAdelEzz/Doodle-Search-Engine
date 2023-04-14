import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


class url_document {
    String uid; // generated encode
    String url; // url
    String _id; // mongo id
    int indexer_visited; // indexer flag
    int sid; // to make it bfs auto incremental
    int crawler_visited; // crawler flag
};

class url_tag {
    int id;
    String uid;
    String tagname;
    String Content;
}



public class Indexer_Filter {

    public static boolean isParsableAsInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String FilterContent(String S) {
        S = S.replaceAll("[^a-zA-Z0-9 ]", "");

        if (isParsableAsInt(S.replaceAll("[ ]","")))
        {
            S = "";
        }

        S = S.toLowerCase();
        return S;
    }

    public static ArrayList<String> get_tag_names(Document page)
    {
        ArrayList<String> tagnames = new ArrayList<>();

        Elements s = page.getElementsByTag("body");
        Element body = s.get(0);
        s = body.getAllElements();
        for (Element e : s)
        {
            int i = 0;
            for (i = 0; i < tagnames.size(); i++)
            {
                if (tagnames.get(i) == e.tagName())
                {
                    break;
                }
            }
            if (i == tagnames.size())
            {
                tagnames.add(e.tagName());
            }
        }
        return tagnames;
    }


    public static void main(String[] args) throws IOException {


        // change the url to any page you want

        Mongod mongo = new Mongod();
        url_document row = mongo.get_indexer_filter_input();

        String url = row.url;

        if(url == null) {
            System.out.println("Not Found");
            return;
        }
        // to save the number of elements of the same type in one page
        HashMap<String, Integer> cnt = new HashMap<String, Integer>();

        Document page = Jsoup.connect(url).get();

        ArrayList<String> tags = get_tag_names(page);

        //System.out.println(page.body() + "\n ================================= \n");

        //================================================================================================
        // used to encrypt the content of the url
        /*
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // change page.body ... to yourstring.getbytes
        byte[] hash = digest.digest(page.body().toString().getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        String sha256String = hexString.toString();
        System.out.println("SHA-256 Hash Value: " + sha256String);

         */
        //================================================================================================


        ArrayList<url_tag> mp = new ArrayList<url_tag>();

        int Randomid = 0;

        for (int i = 0; i < tags.size(); i++) {
            String Query = tags.get(i);
            Elements E = page.select(Query);
            //TableStruct Arr[] = new TableStruct[E.size()];

            for (int j = 0; j < E.size(); j++) {
                String Content = FilterContent(E.get(j).ownText());

                Content = FilterContent(Content);

                if (Content == "")
                    continue;

                cnt.putIfAbsent(tags.get(i), 0);
                cnt.put(tags.get(i), cnt.get(tags.get(i)) + 1);

                url_tag ut = new url_tag();
                ut.uid = row.uid;
                ut.id = cnt.get(tags.get(i));
                ut.tagname = tags.get(i);
                ut.Content = Content;

                mongo.insert_into_db("tags_content", ut);
                mp.add(ut);
            }
        }

        indexer i = new indexer(mp);
        i.main();
    }
}
