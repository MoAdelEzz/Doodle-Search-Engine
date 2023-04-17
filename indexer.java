import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Scanner;

import opennlp.tools.stemmer.PorterStemmer;


class kareem {
    String word;
    String pageID;

    int tagID;

    int count;
};


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
        for (int i = 0; i < table.size(); i++) {
            url_tag tempTable = table.get(i);
            Integer tempID = tempTable.id;
            String tempEncodedURL = tempTable.uid;
            String tempContent = tempTable.Content;
            String tempTagType = tempTable.tagname;
            HashMap<String, Integer> idx = new HashMap<String, Integer>();


            String tempWord = "";
            for (int j = 0; j < tempContent.length() + 1; j++) {
                if ((j == tempContent.length() || tempContent.charAt(j) == ' ') && !isStopWord(tempWord)) {
                    tempWord = stemming.stem(tempWord);
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
        
    }


    @Override
    public void run() {
        //main();
    }

    public boolean isStopWord(String s) throws FileNotFoundException {
        File file = new File("stopwords.txt");
        boolean check = false;
        final Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            final String lineFromFile = scanner.nextLine();
            if (lineFromFile == s) {
                check = true;
                break;
            }
        }
        return check;
    }
}
