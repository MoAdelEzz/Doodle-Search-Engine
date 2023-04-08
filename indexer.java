import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


public class indexer implements Runnable {

    static HashMap<String, HashMap<String, ArrayList<Integer>>> idx = new HashMap<String, HashMap<String, ArrayList<Integer>>>();
    static ArrayList<TableStructISA> table;
    ArrayList<String> stopWords;

    public indexer(ArrayList<TableStructISA> ar) {
        this.table = ar;
        stopWords.add("a");
        stopWords.add("about");
        stopWords.add("above");
        stopWords.add("actually");
        stopWords.add("after");
        stopWords.add("again");
        stopWords.add("against");
        stopWords.add("all");


    }

    public static void main(String args[]) {
        for (int i = 0; i < table.size(); i++) {
            TableStructISA tempTable = table.get(i);
            Integer tempID = tempTable.id;
            String tempEncodedURL = tempTable.EncodedURL;
            String tempContent = tempTable.Content;
            String tempTagType = tempTable.TagType;

            String tempWord = "";
            for (int j = 0; j < tempContent.length(); j++) {
                if (tempContent.charAt(j) == ' ') {

                    if (idx.containsKey(tempWord)) {
                        HashMap<String, ArrayList<Integer>> tempMap = idx.get(tempWord);
                        if (tempMap.containsKey(tempEncodedURL)) {
                            ArrayList<Integer> tempList = tempMap.get(tempEncodedURL);
                            tempList.add(tempID);
                            tempMap.putIfAbsent(tempEncodedURL, tempList);
                            idx.putIfAbsent(tempWord, tempMap);
                        } else {
                            ArrayList<Integer> tempList = new ArrayList<Integer>();
                            tempList.add(tempID);
                            tempMap.put(tempEncodedURL, tempList);
                            idx.putIfAbsent(tempWord, tempMap);
                        }


                    } else {
                        HashMap<String, ArrayList<Integer>> tempMap = new HashMap<String, ArrayList<Integer>>();
                        ArrayList<Integer> tempList = new ArrayList<Integer>();
                        tempList.add(tempID);
                        tempMap.put(tempEncodedURL, tempList);
                        idx.put(tempWord, tempMap);
                    }

                } else {
                    tempWord = tempWord.concat(String.valueOf(tempContent.charAt(j)));
                }
            }
        }

    }


    @Override
    public void run() {

    }
}
