import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import opennlp.tools.stemmer.PorterStemmer;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import javax.print.Doc;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class queryEngine {
    static PorterStemmer stemming = new PorterStemmer();
    static public void main(String[] args) {
        String s = "call";//
        String[] arr = s.split(" ");
        Mongod mongod = new Mongod();
        mongod.start_server();

        HashMap<String, ArrayList<Pair<String, Double>>> prioTable =
                new HashMap<String, ArrayList<Pair<String, Double>>>(); // pageID , string

        PriorityQueue<Pair<String, Double>> res =
                new PriorityQueue<Pair<String, Double>>(Collections.reverseOrder(Comparator.comparing(Pair::getElement1)));//website,priority

        double _ofallDocs = mongod.db.getCollection("urls")
                .countDocuments();
        //////////////////////        IDF              ////////////////////

        for (int i = 0; i < arr.length; i++) {

            String curLetter = arr[i].toLowerCase();
            curLetter = stemming.stem(curLetter);
            //System.out.println(_ofallDocs);

            System.out.println(curLetter);

            /* modified this for paimon angry talking speed
            ArrayList<String> _DocsHaveMyWordIDs
                    = mongod.db.getCollection("indexerTable")
                    .distinct("pageID", Filters.eq("word", curLetter), String.class)
                    .into(new ArrayList<>());
            */

            ArrayList<Document> _DocsHaveMyWordIDs = mongod.db.getCollection("indexerTable")
                    .find(Filters.eq("word",curLetter)).into( new ArrayList<Document>());

            //System.out.println(_DocsHaveMyWordIDs.size());
            double IDF = Math.log(_ofallDocs / _DocsHaveMyWordIDs.size());
            //System.out.printf("the idf of %s %.5f \n", arr[i], IDF);
            System.out.println("Found " + _DocsHaveMyWordIDs.size() + " url");
//            for (String d : _DocsHaveMyWordIDs) {
//                //System.out.printf("found on doc %s \n", d);//d.get("word")
//            }

            //////////////        TF              ////////////////////
            for (Document doc : _DocsHaveMyWordIDs) {

                String d = doc.getString("pageID");

                /* for paimon speed
                int _ofMyWord2 = mongod.db.getCollection("indexerTable")
                        .find(Filters.and(Filters.eq("word", curLetter),
                                Filters.eq("pageID", d))).first().getInteger("count");
                */

                int _ofMyWord2 = doc.getInteger("count");

                int countOfWords = mongod.db.getCollection("urls")
                        .find(Filters.eq("uid", d)).first().getInteger("total_words");


                double TF = (double) _ofMyWord2 /(double) countOfWords;
                if (TF >= 0.5) {
                    TF = 0;
                }
                //System.out.print("the word found in this doc " + _ofMyWord2 + " Times\n");
                //System.out.printf("num of words in doc %s is %.5f and TF of word %s is %.5f\n", d, _ofWordsInDoc, curLetter, TF);//d.get("word")

                //////////   Add TF to IDF and put them in the hash map of each website made a priority of the given query  /////////
                double wordPriority = IDF * TF;
                //System.out.printf("word %s priority in doc %s is %.5f\n", curLetter, d, wordPriority);

                if (prioTable.get(d) == null)
                    prioTable.put(d, new ArrayList<Pair<String, Double>>());

                prioTable.get(d).add(new Pair<String, Double>(curLetter, wordPriority));
                System.out.printf("%s hash %s has priority %.5f\n", d,prioTable.get(d).get(0).getElement0(), prioTable.get(d).get(0).getElement1());

            }
        }
        for (Map.Entry<String, ArrayList<Pair<String, Double>>> entry : prioTable.entrySet()) {
            double sum = 0;
            for (int i = 0; i < entry.getValue().size(); i++) {
                sum += entry.getValue().get(i).getElement1();
            }
            res.add(new Pair<String, Double>(entry.getKey(), sum));
        }
        while (!res.isEmpty()) {
            Pair p = res.remove();
            System.out.print("page ID " + mongod.db.getCollection("urls").find(Filters.eq("uid", p.getElement0())).first().get("url") + " with priority " + p.getElement1() + "\n");
        }
        return;
    }

}