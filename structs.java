
class url_document {
    String uid; // generated encode
    String url; // url
    String _id; // mongo id
    int indexer_visited; // indexer flag
    int sid; // to make it bfs auto incremental
    int crawler_visited; // crawler flag
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


