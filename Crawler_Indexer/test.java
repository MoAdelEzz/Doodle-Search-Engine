import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class test
{
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        Mongod mongo = new Mongod();
        crawler c = new crawler(mongo);


        String s = "https://www.facebook.com/fundraisers";
        Document d = Jsoup.connect(s).get();

        System.out.println(d);
        System.out.println(c.encryptText(s));

    }

}
