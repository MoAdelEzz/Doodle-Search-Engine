import java.util.ArrayList;
import java.util.Scanner;

public class main_engine {
    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);
        System.out.print("Indexer #Threads = ");
        int ThreadCnt = s.nextInt();

        System.out.println("Hello");
        Mongod m = new Mongod();
        m.start_server();


        ArrayList<Thread> T = new ArrayList<Thread>();

        for (int i = 0; i < ThreadCnt; i++)
        {
            Thread t = new Thread(new Indexer_Filter(m));
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

        for (String sui : m.mongooooooooooo)
        {
            System.out.println(s);
        }

        m.close_server();
    }
}
