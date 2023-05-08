package dev.SearchEngine.SearchEngine;

import org.bson.types.ObjectId;

import java.util.ArrayList;

class url_document {
    String uid; // generated encode
    String url; // url
    ObjectId _id; // mongo id
    int indexer_visited; // indexer flag
    int sid; // to make it bfs auto incremental
    int crawler_visited; // crawler flag

    ArrayList<String> urls_points_to_this_document;
};

class url_tag {
    int id;
    String uid;
    String tagname;
    String Content;
}


class WordData{
    String word;
    Double prio;
    ArrayList<Integer> tags;

    public WordData(String word, Double prio, ArrayList<Integer> tags) {
        this.word = word;
        this.prio = prio;
        this.tags = tags;
    }
}

class QueryResults {
    String url;
    String title;
    String paragraph;
    double priority;

    public QueryResults(String url, String title, String paragraph) {
        this.url = url;
        this.title = title;
        this.paragraph = paragraph;
    }
    public double getPriority() {
        return priority;
    }
}

 class Pair<K, V> {

    private final K element0;
    private final V element1;

    public static <K, V> Pair<K, V> createPair(K element0, V element1) {
        return new Pair<K, V>(element0, element1);
    }

    public Pair(K element0, V element1) {
        this.element0 = element0;
        this.element1 = element1;
    }

    public K getElement0() {
        return element0;
    }

    public V getElement1() {
        return element1;
    }

}


