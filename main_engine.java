import java.util.ArrayList;
import java.util.Scanner;

public class main_engine {
    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);
        System.out.print("Indexer #Threads = 1");
        //int ThreadCnt = s.nextInt();
        int ThreadCnt = 10;
        System.out.println("Hello Crawler");
        Mongod m = new Mongod();
        m.start_server();




        ArrayList<Thread> T = new ArrayList<Thread>();

        for (int i = 0; i < ThreadCnt; i++)
        {
            // uncomment this for indexer
            Thread t = new Thread(new Indexer_Filter(m));

            // uncomment this for crawler
            //Thread t = new Thread(new crawler(m));
            T.add(t);
            t.start();
        }

        for (int i = 0; i < ThreadCnt; i++)
        {
            try {
                T.get(i).join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        m.close_server();
    }
}
