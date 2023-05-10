import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class searchCall {
    public static void main(String[] args) {
        HashMap<String, ArrayList<WordData>> prioTable =
                new HashMap<String, ArrayList<WordData>>(); // pageID , string

        Scanner s = new Scanner(System.in);
        System.out.println("aghuhhhhhhhhhh");
        Mongod m = new Mongod();
        m.start_server();

        System.out.print("Enter no of Threads = ");
        int ThreadCnt = s.nextInt();

        System.out.print("Search for : ");
        String query = s.next();


        queryEngine q = new queryEngine(m,null,ThreadCnt,prioTable,null,0);
        long ll = System.nanoTime();
        q.main(query);
        long lll = System.nanoTime();
        System.out.println("time taken = "+ (lll - ll)*1e-9);
    }
}
