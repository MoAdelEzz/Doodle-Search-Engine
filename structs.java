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


