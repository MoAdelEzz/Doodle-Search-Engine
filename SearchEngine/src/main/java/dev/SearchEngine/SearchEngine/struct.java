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

class Human{
    String name;
    public Human(String name) {
        this.name = name;
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


