package dev.SearchEngine.SearchEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class searchCall {

    static String query = null;
    static int ThreadCnt = 0;
    searchCall(int Num_of_threads, String s)
    {
        ThreadCnt = Num_of_threads;
        query = s;
    }
    public static ArrayList<String> main() {
        HashMap<String, ArrayList<Pair<String, Double>>> prioTable =
                new HashMap<String, ArrayList<Pair<String, Double>>>(); // pageID , string

        Mongod m = new Mongod();
        m.start_server();
        /*
        Scanner s = new Scanner(System.in);
        System.out.println("call");

        System.out.print("Enter no of Threads = ");
        int ThreadCnt = s.nextInt();

        System.out.print("Search for : ");
        String query = s.next();
        */

        queryEngine q = new queryEngine(m,null,ThreadCnt,prioTable,null,0);
        return q.main(query);
    }
}