package dev.SearchEngine.SearchEngine;
import com.mongodb.client.model.Filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Filter;

public class searchCall {
    static String query = null;
    static int ThreadCnt = 1;
    searchCall(int thread_count, String query)
    {
        this.ThreadCnt = thread_count;
        this.query = query;
    }

    public static ArrayList<String> main(String[] args) {
        HashMap<String, ArrayList<WordData>> prioTable =
                new HashMap<String, ArrayList<WordData>>(); // pageID , string
        Mongod m = new Mongod();
        m.start_server();

        if (query == null) {
            Scanner s = new Scanner(System.in);
            System.out.println("aghuhhhhhhhhhh");

            System.out.print("Enter no of Threads = ");
            ThreadCnt = s.nextInt();
            s.nextLine();

            System.out.print("Search for : ");
            query = s.nextLine();
        }


        queryEngine q = new queryEngine(m,null,ThreadCnt,prioTable,null,0);
        q.main(query);

        ArrayList<String> res = new ArrayList<>();
        for (String url : prioTable.keySet())
        {
            res.add(m.db.getCollection("urls").find(Filters.eq("uid",url)).first().getString("url"));
        }
        return res;
    }
}
