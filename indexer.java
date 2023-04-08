import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;


public class indexer implements Runnable {

    static HashMap<String, HashMap<String, ArrayList<Integer>>> idx = new HashMap<String, HashMap<String, ArrayList<Integer>>>();
    static ArrayList<TableStruct> table;
    ArrayList<String> stopWords;

    public indexer(ArrayList<TableStruct> ar) {
        this.table = ar;
        stopWords = new ArrayList<>();
        stopWords.add("a");
        stopWords.add("about");
        stopWords.add("above");
        stopWords.add("actually");
        stopWords.add("after");
        stopWords.add("again");
        stopWords.add("against");
        stopWords.add("all");


    }

    public void main() {
        for (int i = 0; i < table.size(); i++) {
            TableStruct tempTable = table.get(i);
            Integer tempID = tempTable.id;
            String tempEncodedURL = tempTable.EncodedURL;
            String tempContent = tempTable.Content;
            String tempTagType = tempTable.TagType;


            String tempWord = "";
            for (int j = 0; j < tempContent.length() + 1; j++) {
                if (j == tempContent.length() || tempContent.charAt(j) == ' ') {
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
                    // by moa
                    tempWord = "";
                } else {
                    tempWord = tempWord.concat(String.valueOf(tempContent.charAt(j)));
                }
            }
        }

        Set<String> s = idx.keySet();

        System.out.println(s);
    }


    @Override
    public void run() {
        main();
    }
}