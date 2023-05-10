import com.mongodb.client.model.Filters;
import opennlp.tools.stemmer.PorterStemmer;
import org.bson.Document;

import javax.print.Doc;
import java.util.*;


public class queryEngine implements Runnable{
    private  enum Operation {
        CALCTF,
        GETTAGS,
    }
    private Operation Ope ;
    static PorterStemmer stemming = new PorterStemmer();
    Mongod mongod = null;
    String letter = null;
    int threadCount;
    ArrayList<Document> _DocsHaveMyWordIDs;
    HashMap<String, ArrayList<WordData>> prioTable = null;


    PriorityQueue<QueryResults> res =
            new PriorityQueue<QueryResults>(Collections.reverseOrder(Comparator.comparing(QueryResults::getPriority)));//website,priority

    double IDF;
    queryEngine(Mongod m,String s,int threadCount,HashMap<String, ArrayList<WordData>> prioTable , ArrayList<Document> _DocsHaveMyWordIDs , double IDF)
    {
        this.mongod = m;
        this.letter = s;
        this.threadCount = threadCount;
        this.prioTable = prioTable;
        this._DocsHaveMyWordIDs =_DocsHaveMyWordIDs;
        this.IDF = IDF;
    }
    @Override
    public  void run(){
        //////// Get the priority of each website //////////
        switch (Ope){
            case CALCTF:
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
                            prioTable.put(d, new ArrayList<WordData>());

                        prioTable.get(d).add(new WordData(letter, wordPriority,(ArrayList<Integer>)doc.get("tagID")));
                        System.out.printf("%s hash %s has priority %.5f\n", d, prioTable.get(d).get(0).word, prioTable.get(d).get(0).prio);
                    }


                }
                break;
            case GETTAGS:
                int jj =Integer.parseInt(Thread.currentThread().getName());
                int size2 = prioTable.size();
                int part2 = (int)size2 % threadCount;
                int step2 = size2 / threadCount;
                int first2 = step2 * jj;
                int second2 = step2 * (1 + jj) - 1;
                if ((jj == (threadCount - 1)) && (part2 > 0))
                {
                    second2 += part2;
                }

                jj = first2;
                //SortedSet<Integer> set = new TreeSet<>();




                for (Map.Entry<String, ArrayList<WordData>> entry : prioTable.entrySet()) {
                    if (jj >= first2 && jj <= (second2)){
                        HashMap<Integer, Integer> sortingTags
                                = new HashMap<Integer, Integer>();// tag itself , pri of tag

                        for (int i = 0; i < entry.getValue().size(); i++) {
                            for (int k:entry.getValue().get(i).tags) {

                                if (sortingTags.containsKey(k)) {
                                    int val = sortingTags.get(k);
                                    val++;
                                    sortingTags.replace(k,val);
                                }else {
                                    sortingTags.putIfAbsent(k,1);
                                }
                            }
                        }

                        Map.Entry<Integer, Integer> maxEntry = null;
                        Map.Entry<Integer, Integer> maxEntry2 = null;

                        for (Map.Entry<Integer, Integer> entry2 : sortingTags.entrySet())
                        {
                            if (maxEntry == null || entry2.getValue().compareTo(maxEntry.getValue()) > 0)
                            {
                                maxEntry = entry2;
                            }
                        }
                        sortingTags.remove(maxEntry.getKey());

                        for (Map.Entry<Integer, Integer> entry2 : sortingTags.entrySet())
                        {
                            if (maxEntry2 == null || entry2.getValue().compareTo(maxEntry2.getValue()) > 0)
                            {
                                maxEntry2 = entry2;
                            }
                        }

                        Document d1 = mongod.db.getCollection("tags_content")
                                .find(Filters.
                                        and(Filters.eq("uid", entry.getKey()), Filters.eq("id", maxEntry.getKey()))).first();

                        System.out.println(maxEntry);

                        if(maxEntry2 != null)
                            System.out.println(maxEntry2);

                        //System.out.println(d1);

                        double sum = 0;
                        for (int i = 0; i < entry.getValue().size(); i++) {
                            sum += entry.getValue().get(i).prio;
                        }

                        synchronized (mongod.lock) {
                            //res.add(new Pair<String, Double>(entry.getKey(), sum));
                            //res.add(new QueryResults(entry.getKey(),))
                        }
                    }else {
                        continue;
                    }
                    jj++;
                }

                break;

            default:
                break;
        }


    }
    public void main(String s) {
        String[] arr = s.split(" ");
        mongod.start_server();



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

            //////////////////////////// USING THREADS INSIDE LOOP /////////////////////////////////
            ArrayList<Thread> threads_array = new ArrayList<Thread>();

            for (int j = 0;  j < threadCount; j++) {
                queryEngine q = new queryEngine(mongod,curLetter,threadCount,prioTable,_DocsHaveMyWordIDs,IDF);
                q.Ope = Operation.CALCTF;
                Thread T = new Thread(q);
                T.setName(Integer.toString(j));
                threads_array.add(T);
            }

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
            ////////////////////////////////////////////////////////////////////////////////////////////
        }

        lunchThreads(Operation.GETTAGS);


//        for (Map.Entry<String, ArrayList<WordData>> entry : prioTable.entrySet()) {
//
//        }
//        while (!res.isEmpty()) {
//            Pair p = res.remove();
//            System.out.print("page ID " + mongod.db.getCollection("urls").find(Filters.eq("uid", p.getElement0())).first().get("url") + " with priority " + p.getElement1() + "\n");
//        }

        return;
    }

    private void lunchThreads(Operation o) {
        ArrayList<Thread> threads_array = new ArrayList<Thread>();

        for (int j = 0;  j < threadCount; j++) {
            queryEngine q = new queryEngine(mongod,null,threadCount,prioTable,null,0);
            q.Ope = o;
            Thread T = new Thread(q);
            T.setName(Integer.toString(j));
            threads_array.add(T);
        }

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

}
