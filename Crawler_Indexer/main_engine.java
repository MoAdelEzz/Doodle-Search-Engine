
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class main_engine {
    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);
        System.out.println("initiating code 007 ... stand by");
        Mongod m = new Mongod();
        m.start_server();

        System.out.print("Select #Thread = ");
        int ThreadCnt = s.nextInt();

        System.out.print("Crawler -c- / indexer -i- => ");
        char type = (char) s.next().charAt(0);

        System.out.println(type);
        if (type == 'c')
        {
            crawler c = new crawler(m);
            c.main(ThreadCnt);
        }
        else
        {
            Indexer_Filter I = new Indexer_Filter(m);
            I.main(m,ThreadCnt);
        }

        //driver.quit();

        m.close_server();
    }
}
