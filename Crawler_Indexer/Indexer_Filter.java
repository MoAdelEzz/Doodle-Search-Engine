import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class Indexer_Filter implements Runnable {

    Mongod mongo = null;
    Indexer_Filter(Mongod m)
    {
        mongo = m;
    }
    @Override
    public void run() {
        try {
            start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isParsableAsInt(String input) {
        try {
            Long.parseLong(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String FilterContent(String S) {

        String Filtered = "";
        String[] Words = S.split(" ");
        for (String Word : Words)
        {
            int begin = 0;
            while (begin < Word.length() && Word.charAt(begin) == ' ') begin++;
            Word = Word.substring(begin);

            if (Word.replaceAll("[a-zA-Z]","").length() != 0) continue;

            Filtered = Filtered.concat(" " + Word);
        }
        return Filtered;
    }

    public ArrayList<String> get_tag_names(Document page)
    {
        ArrayList<String> tagnames = new ArrayList<>();

        Elements s = page.getElementsByTag("body");

        if (s.size() == 0)
        {
            return null;
        }
        Element body = s.get(0);
        s = body.getAllElements();
        for (Element e : s)
        {
            if (e.tagName().equals("script") || e.tagName().equals("noscript"))
                continue;

            int i = 0;
            for (i = 0; i < tagnames.size(); i++)
            {
                if (tagnames.get(i).equals(e.tagName()))
                {
                    break;
                }
            }
            if (i == tagnames.size())
            {
                tagnames.add(e.tagName());
            }
        }
        tagnames.add("meta");
        return tagnames;
    }

    public void main(Mongod m, int _of_threads)
    {
        ArrayList<Thread> T = new ArrayList<Thread>();
        for (int i = 0; i < _of_threads; i++)
        {
            Thread t = new Thread(new Indexer_Filter(m));
            T.add(t);
            t.start();
        }

        for (int i = 0; i < _of_threads; i++)
        {
            try {
                T.get(i).join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void start() throws IOException {

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
                page = Jsoup.connect(url).timeout(3000).get();
            }
            catch (Exception e)
            {
                continue;
            }
            
            // get all tags mentioned in the page
            ArrayList<String> tags = get_tag_names(page);
            System.out.println("total tags count = " + tags.size());

            if (tags == null) continue;

            ArrayList<url_tag> mp = new ArrayList<>();

            int tags_count = 0;

            // for each tag of the page
            for (String tag_name : tags)
            {
                Elements E;
                try
                {
                    E = page.select(tag_name);
                } catch (Exception z) {
                    continue;
                }

                for (Element element : E) {
                    // filter the content of the current processing tag
                    String Content = null;

                    if (tag_name.equals("meta"))
                    {
                        Content = element.attr("content");
                    }
                    else
                    {
                        Content = element.ownText();
                    }

                    // empty tag case
                    if (Content.equals("")) continue;

                    String Filtered_Content = FilterContent(Content);

                    if (!Filtered_Content.equals(""))
                    {
                        // then it's good to go
                        url_tag ut = new url_tag(tags_count,row.uid,tag_name,Filtered_Content,Content);

                        synchronized (mongo.lock)
                        {
                            org.bson.Document obj = new org.bson.Document();
                            obj.put("id",ut.id);
                            obj.put("uid",ut.uid);
                            obj.put("tagname",ut.tagname);
                            obj.put("Content",ut.Original_Content);
                            mongo.insert_indexer_filter_object(obj);
                        }

                        mp.add(ut);
                        tags_count++;
                    }
                }
            }

            indexer i = new indexer(mongo);
            i.main(mp);
        }
    }
}
