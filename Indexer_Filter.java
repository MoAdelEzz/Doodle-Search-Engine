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

class TableStructISA
{
    int id;
    String EncodedURL;
    String Content;
    String TagType;

    TableStructISA(int ID,String u,String c,String t)
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

    public static void KareemAlaaFunc(ArrayList mp)
    {

    }

    public static void main(String[] args) throws IOException {

        // change the url to any page you want
        String url = "https://stackoverflow.com/questions/6028724/adding-an-external-jar-library-to-intellij-idea";

        // all possible tags i thought about till now
        // updatable
        String tags[] = {"h1","h2","h3","h4","h5","h6","p","a","div","small","td","label","span","li","section","strong","tr"};
        Document page = Jsoup.connect(url).get();

        ArrayList mp = new ArrayList();

        int Randomid = 0;

        for (int i = 0; i < tags.length; i++)
        {
            String Query = tags[i]+":not(:has(*))";
            Elements E = page.select(Query);
            TableStructISA Arr[] = new TableStructISA[E.size()];

            for (int j = 0; j < E.size(); j++)
            {
                String Content = FilterContent(E.get(j).text());

                if (Content == "")
                    break;

                Arr[j] = new TableStructISA(Randomid,url,Content,tags[i]);
                Randomid++;
            }

            mp.add(Arr);
        }

        // mp Now Contain Broken html page to the second part of indexer
        KareemAlaaFunc(mp);

        return ;

    }
}
