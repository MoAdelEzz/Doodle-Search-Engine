import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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

public class Indexer_Filter {

    HashMap<String,String> Site;

    public static String FilterContent(String S)
    {
        // to do : implement filtering later
        return S;
    }

    public static void KareemAlaaFunc(ArrayList<TableStruct> mp)
    {
        Thread T = new Thread(new indexer(mp));
        T.start();
        try {
            T.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {

        // change the url to any page you want
        String url = "https://codeforces.com/";

        // all possible tags i thought about till now
        // updatable
        String tags[] = {"h1","h2","h3","h4","h5","h6","p","a","div","small","td","label","span","li","section","strong","tr"};
        Document page = Jsoup.connect(url).get();

        ArrayList<TableStruct> mp = new ArrayList<TableStruct>();

        int Randomid = 0;

        for (int i = 0; i < tags.length; i++)
        {
            String Query = tags[i]+":not(:has(*))";
            Elements E = page.select(Query);
            TableStruct Arr[] = new TableStruct[E.size()];

            for (int j = 0; j < E.size(); j++)
            {
                String Content = FilterContent(E.get(j).text());

                if (Content == "")
                    break;

                mp.add(new TableStruct(Randomid,url,Content,tags[i]));
                Randomid++;
            }
        }

        // mp Now Contain Broken html page to the second part of indexer
        KareemAlaaFunc(mp);

        return ;

    }
}
