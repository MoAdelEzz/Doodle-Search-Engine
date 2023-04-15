import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
public class crawler {
    private static HashMap<String, Boolean> unique_txt_of_pages = new HashMap<String, Boolean>();
    private static HashMap<String, Boolean> unique_urls = new HashMap<String, Boolean>();
    private static ArrayList<String> links_list = new ArrayList<>(
            Arrays.asList(
                    "https://www.dw.com/en/top-stories/s-9097",
                    "https://www.bbc.com/",
                    "https://www.cnbc.com/world/?region=world"
            ));

    public static void main(String[] args) {
        get_links_of_page();
    }

    static void get_links_of_page() {
        int i = 0;
        int j = 0;
        while (links_list.size() < 5000) {
            try {
                String current_url = links_list.get(i);
                Document doc = Jsoup.connect(current_url).get();
                String title = doc.title();
                System.out.println("title: " + title);
                String S = doc.text();

                // System.out.println(S.split("Premier League",-1).length - 1) ;
                Elements links = doc.select("a[href]");

                if (check_repeated_txt(S)) {
                    for (Element link : links) {

                        String link_url = link.attr("href");
                        if (!unique_urls.containsKey(link_url)) {
                            if(!link_url.contains("https"))
                                link_url = current_url + link_url;

                            System.out.println("\nlink :" + link_url + "  j:" + j++ + "   i: " + i);
                            links_list.add(link_url);
                            unique_urls.put(link_url, true);
                        }

                        // System.out.println("text: " + link.text());
                    }
                }
                i++;
            } catch (IOException ex) {
                System.out.println("Error");
            }
        }
    }

    static Boolean check_repeated_txt(String web_txt) {


        String[] page_words = web_txt.split(" ");

        String sum_of_first_chars = new String();

        for (String word : page_words) {
            if(word.length() > 0)
                sum_of_first_chars += word.charAt(0);
        }

        System.out.println(sum_of_first_chars.length());

        if (!unique_txt_of_pages.containsKey(sum_of_first_chars)) {
            unique_txt_of_pages.put(sum_of_first_chars, true);
            return true;
        } else {
            return false;
        }
    }

}


// take one link from  the database
//