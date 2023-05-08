import org.bson.types.ObjectId;
import java.util.ArrayList;

class url_document {
    String uid; // generated encode
    String url; // url
    ObjectId _id; // mongo id
    int indexer_visited; // indexer flag
    int sid; // to make it bfs auto incremental
    int crawler_visited; // crawler flag

    double priority;
};

class url_tag {
    int id;
    String uid;
    String tagname;
    String Content;

    String Original_Content;

    public url_tag(int id, String uid, String tagname, String content,String Original) {
        this.id = id;
        this.uid = uid;
        this.tagname = tagname;
        this.Content = content;
        this.Original_Content = Original;
    }
}

class kareem {
    String word;
    String pageID;

    ArrayList<Integer> tagID;

    int count;
};