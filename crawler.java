import java.io.IOException;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

//test
public class crawler
{
    public static void main(String args[])
    {

        try{
        Document doc = Jsoup.connect("https://codeforces.com").get();
        String title = doc.title();
        System.out.println("title: " + title);
        //String S = doc.text();
        
        //System.out.println(S.split("Premier League",-1).length - 1) ;
        
        Elements links =  doc.select("a[href]");
        
        for(Element link: links)
        {
            System.out.println("\nlink :" + link.attr("href"));
           //System.out.println("text: " + link.text());
        }

        }
        catch(IOException ex)
        {
            System.out.println("A7a");
        }

    }
}