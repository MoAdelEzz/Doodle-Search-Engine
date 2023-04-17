import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Indexer_Filter implements Runnable {

    Mongod mongo = null;
    Indexer_Filter(Mongod m)
    {
        mongo = m;
    }
    @Override
    public void run() {
        try {
            main();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isParsableAsInt(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String FilterContent(String S) {
        S = S.replaceAll("[^a-zA-Z0-9 ]", "");

        if (isParsableAsInt(S.replaceAll("[ ]","")))
        {
            S = "";
        }

        S = S.toLowerCase();
        return S;
    }

    public ArrayList<String> get_tag_names(Document page)
    {
        ArrayList<String> tagnames = new ArrayList<>();

        Elements s = page.getElementsByTag("body");
        Element body = s.get(0);
        s = body.getAllElements();
        for (Element e : s)
        {
            if (e.tagName() == "script" || e.tagName() == "noscript")
                continue;

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


     public void main() throws IOException {

        // change the url to any page you want
        while(true) {

            url_document row = null;

            synchronized (mongo.lock) {
                row = mongo.get_indexer_filter_input();
            }


            if (row == null) {
                System.out.println("No Urls To Index :)");
                return;
            }

            System.out.println("Working On " + row.url);
            String url = row.url;

            if (url == null) {
                System.out.println("Not Found");
                return;
            }
            // to save the number of elements of the same type in one page

            Document page;
            try {
                page = Jsoup.connect(url).get();
            }
            catch (Exception e)
            {
                continue;
            }

            ArrayList<String> tags = get_tag_names(page);


            ArrayList<url_tag> mp = new ArrayList<url_tag>();

            int Randomid = 0;

            for (int i = 0; i < tags.size(); i++) {

                String Query = tags.get(i);

                Elements E = page.select(Query);

                for (int j = 0; j < E.size(); j++) {
                    String Content = FilterContent(E.get(j).ownText());
                    if (Content == "")
                        continue;

                    Content = FilterContent(Content);

                    if (Content.length() == 0)
                        continue;

                    if (Content != ""){
                    url_tag ut = new url_tag();
                    ut.uid = row.uid;
                    ut.id = Randomid;
                    ut.tagname = tags.get(i);
                    ut.Content = Content;

                    synchronized (mongo.lock) {
                        mongo.insert_into_db("tags_content", ut);
                    }

                    mp.add(ut);
                    Randomid++;
                    }
                }
            }
            indexer i = new indexer(mongo);
            i.main(mp);
        }
    }
}
