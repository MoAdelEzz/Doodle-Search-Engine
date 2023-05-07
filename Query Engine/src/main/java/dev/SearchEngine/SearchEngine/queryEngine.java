package dev.SearchEngine.SearchEngine;

import com.mongodb.client.model.Filters;
import opennlp.tools.stemmer.PorterStemmer;
import org.bson.Document;

import java.util.*;


public class queryEngine implements Runnable{
    class WordData{
        String word;
        Double prio;
        ArrayList<Integer> tags = new ArrayList<Integer>();
    }
    static PorterStemmer stemming = new PorterStemmer();
    Mongod mongod = null;
    String letter = null;
    int threadCount;
    ArrayList<Document> _DocsHaveMyWordIDs;
    HashMap<String, ArrayList<Pair<String, Double>>> prioTable = null;

    double IDF;
    queryEngine(Mongod m,String s,int threadCount,HashMap<String, ArrayList<Pair<String, Double>>> prioTable , ArrayList<Document> _DocsHaveMyWordIDs , double IDF)
    {
        this.mongod = m;
        this.letter = s;
        this.threadCount = threadCount;
        this.prioTable = prioTable;
        this._DocsHaveMyWordIDs =_DocsHaveMyWordIDs;
        this.IDF = IDF;
    }
    @Override
    public void run(){
        //////// Get the priority of each website //////////
        int j =Integer.parseInt(Thread.currentThread().getName());
        int size = _DocsHaveMyWordIDs.size();
        int part = (int)size % threadCount;
        int step = size / threadCount;
        int first = step * j;
        int second = step * (1 + j) - 1;

        if ((j == (threadCount - 1)) && (part > 0))
        {
            second += part;
        }
        System.out.println( Thread.currentThread().getId() + " "+ Thread.currentThread().getName()+ " " + size + " " + part + " " + step +" " + first +" " + second);
        //////////////        TF              ////////////////////
        for ( j = first; j <= (second); j++) {

            Document doc = _DocsHaveMyWordIDs.get(j);

            String d = doc.getString("pageID");

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

            synchronized (mongod.lock) {
                if (prioTable.get(d) == null)
                    prioTable.put(d, new ArrayList<Pair<String, Double>>());

                prioTable.get(d).add(new Pair<String, Double>(letter, wordPriority));
                System.out.printf("%s hash %s has priority %.5f\n", d, prioTable.get(d).get(0).getElement0(), prioTable.get(d).get(0).getElement1());
            }


        }
    }
    public ArrayList<String> main(String s) {
        String[] arr = s.split(" ");
        mongod.start_server();



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

            _DocsHaveMyWordIDs = mongod.db.getCollection("indexerTable")
                    .find(Filters.eq("word",curLetter)).into( new ArrayList<Document>());

            //System.out.println(_DocsHaveMyWordIDs.size());
            IDF = Math.log(_ofallDocs / _DocsHaveMyWordIDs.size());
            //System.out.printf("the idf of %s %.5f \n", arr[i], IDF);
            System.out.println("Found " + _DocsHaveMyWordIDs.size() + " url");
//            for (String d : _DocsHaveMyWordIDs) {
//                //System.out.printf("found on doc %s \n", d);//d.get("word")
//            }
            ArrayList<Thread> threads_array = new ArrayList<>();

            for (int j = 0;  j < threadCount; j++) {
                Thread T = new Thread(new queryEngine(mongod,curLetter,threadCount,prioTable,_DocsHaveMyWordIDs,IDF));
                T.setName(Integer.toString(j));
                threads_array.add(T);
            }


            System.out.println("here thread count = " + Integer.toString(threadCount));

            // Start each thread in the array
            for (Thread thread : threads_array) {
                thread.start();
            }

            // Join each thread in the array
            for (Thread thread : threads_array) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
        for (Map.Entry<String, ArrayList<Pair<String, Double>>> entry : prioTable.entrySet()) {
            double sum = 0;
            for (int i = 0; i < entry.getValue().size(); i++) {
                sum += entry.getValue().get(i).getElement1();
            }

            res.add(new Pair<String, Double>(entry.getKey(), sum));
        }
        ArrayList<String> myres = new ArrayList<>();
        while (!res.isEmpty()) {
            Pair p = res.remove();
            myres.add(mongod.db.getCollection("urls").find(Filters.eq("uid", p.getElement0())).first().getString("url"));
            System.out.print("page ID " + mongod.db.getCollection("urls").find(Filters.eq("uid", p.getElement0())).first().get("url") + " with priority " + p.getElement1() + "\n");
        }
        return myres;
    }


}