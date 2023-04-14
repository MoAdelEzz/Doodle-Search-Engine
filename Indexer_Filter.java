import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;



import java.lang.reflect.Field;

class TableStruct
{
    int id;
    String EncodedURL;
    String Content;
    String TagType;

    TableStruct(int ID,String u,String c,String t)
    {
        id = ID; EncodedURL = u; Content = c; TagType = t;
    }

}

class url_document
{
    String uid; // generated encode
    String url; // url
    String _id; // mongo id
    int indexer_visited; // indexer flag
    int sid; // to make it bfs auto incremental
    int crawler_visited; // crawler flag
};

class url_tag
{
    int id;
    String uid;
    String tagname;
    String Content;
}

public class Indexer_Filter {

    HashMap<String,String> Site;

    public static String FilterContent(String S)
    {
        S = S.replaceAll("[^a-zA-Z0-9]","");

        S = S.toLowerCase();
        return S;
    }


    public static void main(String[] args) throws IOException, IllegalAccessException {


        // change the url to any page you want

        Mongod mongo = new Mongod();
        url_document row =  mongo.get_indexer_filter_input();

        String url = row.url;
        System.out.println("url = " + url);

        // all possible tags i thought about till now
        // updatable
        String tags[] = {"h1","h2","h3","h4","h5","h6","p","a","div","small","td","label","span","li","section","strong","tr"};

        HashMap<String,Integer> cnt = new HashMap<String, Integer>();

        Document page = Jsoup.connect(url).get();

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

        for (int i = 0; i < tags.length; i++)
        {
            String Query = tags[i];
            Elements E = page.select(Query);
            TableStruct Arr[] = new TableStruct[E.size()];

            for (int j = 0; j < E.size(); j++)
            {
                String Content = FilterContent(E.get(j).ownText());

                Content = FilterContent(Content);

                try {
                    Integer.parseInt(Content);
                    continue;
                }
                catch (Exception Ex)
                {
                    // do nothing => not the whole string are numbers
                }

                if (Content == "")
                    break;

                cnt.putIfAbsent(tags[i],0);
                cnt.put(tags[i],cnt.get(tags[i])+1);

                url_tag ut = new url_tag();
                ut.uid = row.uid;
                ut.id = cnt.get(tags[i]);
                ut.tagname = tags[i];
                ut.Content = Content;

                System.out.println(Content);

                mongo.insert_into_db("tags_content",ut);
                mp.add(ut);
            }
        }


        // mp Now Contain Broken html page to the second part of indexer

        return ;

    }
}
