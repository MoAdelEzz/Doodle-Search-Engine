import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;


class kareem {
    String word;
    String pageID;

    int tagID;

    int count;
};


public class indexer implements Runnable {

    //static ArrayList<url_tag> table;
    Mongod mongodb;
    ArrayList<String> stopWords;

    public indexer(Mongod mongo) {
        synchronized (mongo.lock) {
            mongodb = mongo;
        }

        //this.table = ar;
        stopWords = new ArrayList<>();
        stopWords.add("a");
        stopWords.add("about");
        stopWords.add("above");
        stopWords.add("actually");
        stopWords.add("after");
        stopWords.add("again");
        stopWords.add("against");
        stopWords.add("all");
// test
        // main();
    }

    public void main( ArrayList<url_tag> table)
    {
        for (int i = 0; i < table.size(); i++) {
            url_tag tempTable = table.get(i);
            Integer tempID = tempTable.id;
            String tempEncodedURL = tempTable.uid;
            String tempContent = tempTable.Content;
            String tempTagType = tempTable.tagname;
            HashMap<String, Integer> idx = new HashMap<String, Integer>();


            String tempWord = "";
            for (int j = 0; j < tempContent.length() + 1; j++) {
                if (j == tempContent.length() || tempContent.charAt(j) == ' ') {
                    if (idx.containsKey(tempWord)) {
                        Integer cnt = idx.get(tempWord);
                        idx.put(tempWord, cnt + 1);
                    } else {
                        idx.put(tempWord, 1);
                    }
                    // by moa
                    tempWord = "";
                } else {
                    tempWord = tempWord.concat(String.valueOf(tempContent.charAt(j)));
                }
            }


            for (HashMap.Entry<String, Integer> entry : idx.entrySet()) {
                String temps = entry.getKey();
                Integer tempcnt = entry.getValue();
                kareem insertedTable = new kareem();
                insertedTable.word = temps;
                insertedTable.count = tempcnt;
                insertedTable.pageID = tempEncodedURL;
                insertedTable.tagID = tempID;

                synchronized (mongodb.lock) {
                    mongodb.insert_into_db("indexerTable", insertedTable);
                }
            }


        }

        //Set<String> s = idx.keySet();

        //System.out.println(s);
    }


    @Override
    public void run() {
        //main();
    }
}
