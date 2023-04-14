import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import java.lang.reflect.Field;

class TableStruct {
    int id;
    String EncodedURL;
    String Content;
    String TagType;

    TableStruct(int ID, String u, String c, String t) {
        id = ID;
        EncodedURL = u;
        Content = c;
        TagType = t;
    }

}


public class Indexer_Filter {

    HashMap<String, String> Site;

    public static String FilterContent(String S) {
        S = S.replaceAll("[^a-zA-Z0-9]", "");

        S = S.toLowerCase();
        return S;
    }

    public static void KareemAlaaFunc(ArrayList<TableStruct> mp) {
        Thread T = new Thread(new indexer(mp));
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException, IllegalAccessException {


        // change the url to any page you want
        String url = "https://www.mongodb.com/basics/create-database";

        // all possible tags i thought about till now
        // updatable
        String tags[] = {"h1", "h2", "h3", "h4", "h5", "h6", "p", "a", "div", "small", "td", "label", "span", "li", "section", "strong", "tr"};
        Document page = Jsoup.connect(url).get();

        System.out.println(page.body() + "\n ================================= \n");

        //================================================================================================
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
        //================================================================================================


        ArrayList<TableStruct> mp = new ArrayList<TableStruct>();

        int Randomid = 0;

        for (int i = 0; i < tags.length; i++) {
            String Query = tags[i];
            Elements E = page.select(Query);
            TableStruct Arr[] = new TableStruct[E.size()];

            for (int j = 0; j < E.size(); j++) {
                String Content = FilterContent(E.get(j).ownText());

                Content = FilterContent(Content);

                try {
                    Integer.parseInt(Content);
                    continue;
                } catch (Exception Ex) {
                    // do nothing => not the whole string are numbers
                }

                if (Content == "")
                    break;


                mp.add(new TableStruct(Randomid, url, Content, tags[i]));
                Randomid++;
            }
        }

        // mp Now Contain Broken html page to the second part of indexer
        KareemAlaaFunc(mp);

        return;

    }
}
