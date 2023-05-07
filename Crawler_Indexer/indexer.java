
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Scanner;

import opennlp.tools.stemmer.PorterStemmer;



public class indexer implements Runnable {
    Mongod mongodb;
    PorterStemmer stemming;



    public indexer(Mongod mongo) {
        synchronized (mongo.lock) {
            mongodb = mongo;
        }
        stemming = new PorterStemmer();
    }

    public void main(ArrayList<url_tag> table) throws FileNotFoundException {

        HashMap<String,kareem> h = new HashMap<>();
        int total_words = 0;



        for (url_tag tempTable : table)
        {
            Integer tempID = tempTable.id;
            String tempEncodedURL = tempTable.uid;
            String tempContent = tempTable.Content;
            HashMap<String, Integer> idx = new HashMap<>();

            String[] Words = tempContent.split(" ");

            for (String tempWord : Words)
            {
                tempWord = stemming.stem(tempWord);

                if (tempWord.contains("[^ ]"))
                    continue;

                if (idx.containsKey(tempWord)) {
                    Integer cnt = idx.get(tempWord);
                    idx.put(tempWord, cnt + 1);
                } else {
                    idx.put(tempWord, 1);
                }
            }

            for (HashMap.Entry<String, Integer> entry : idx.entrySet()) {
                String temps = entry.getKey();
                Integer tempcnt = entry.getValue();

                if (h.containsKey(temps)) {
                    h.get(temps).tagID.add(tempID);
                    h.get(temps).count += tempcnt;
                } else {
                    kareem insertedTable = new kareem();
                    insertedTable.tagID = new ArrayList<>();

                    insertedTable.word = temps;
                    insertedTable.count = tempcnt;
                    insertedTable.pageID = tempEncodedURL;
                    insertedTable.tagID.add(tempID);

                    h.put(temps, insertedTable);
                }
                total_words++;
            }
        }

        synchronized (mongodb.lock) {
            for (Map.Entry<String, kareem> e : h.entrySet()) {
                if (e.getValue().word.equals("")) continue;

                mongodb.insert_into_db("indexerTable", e.getValue());
            }
            if (table.size() > 0) {
                mongodb.add_total_words(table.get(0).uid, total_words);
            }
        }


    }


    @Override
    public void run() {
        //main();
    }

    public boolean isStopWord(String s) throws FileNotFoundException {
        File file = new File("stopwords.txt");
        s = s.toLowerCase();
        boolean check = false;
        final Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            final String lineFromFile = scanner.nextLine();
            if (lineFromFile.equals(s)) {
                check = true;
                break;
            }

        }
        return check;
    }
}
