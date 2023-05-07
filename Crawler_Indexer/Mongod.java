

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


public class Mongod {

    int j;
    boolean crawler_init = false;
    final String lock = "";
    String dbname = "search_engine";
    MongoClient client = null;
    MongoDatabase db = null;

    ArrayList<String> mongooooooooooo;


    public void start_server()
    {
        // Set the logging level for the MongoDB driver
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.OFF);
        LogManager.getLogManager().reset();

        // Disable Logback logging output
        System.setProperty("org.slf4j.impl.StaticLoggerBinder", "org.slf4j.impl.OFF");

        client = MongoClients.create("mongodb://localhost:27017");

        db = client.getDatabase("test");
        mongooooooooooo = new ArrayList<String>();

    }

    public void close_server()
    {
        client.close();
    }

    private Document class_to_document(Object obj)
    {
        Document D = new Document();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // allow access to private fields
            String name = field.getName();
            Object value = null;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            D.append(name,value);
            // {id: 0 , Enc..:}
        }

        return D;
    }

    public void insert_into_db(String collection_name, Object obj)
    {
        Document D = class_to_document(obj);
        MongoCollection<Document> col = db.getCollection(collection_name);
        col.insertOne(D);
    }

    public void insert_indexer_filter_object(Document row)
    {
        MongoCollection<Document> col = db.getCollection("tags_content");
        col.insertOne(row);
    }

    public url_document get_indexer_filter_input()
    {
        synchronized (this) {

            Document query = new Document("indexer_visited", 0);
            Document sortby = new Document("sid", 1);

            FindIterable<Document> ret;
            Document resultDocument = null;

            // Malek Output Change later if you need
            MongoCollection col = db.getCollection("urls");

            ret = col.find(query).sort(sortby).limit(1);
            resultDocument = ret.first();

            if (resultDocument == null) {
                return null;
            }

            ObjectId O = new ObjectId(resultDocument.get("_id").toString());
            //String O = resultDocument.get("_id").toString();

            Document q1 = new Document("_id", O);
            Document q2 = new Document("$set", new Document("indexer_visited", 1));

            col.updateOne(q1, q2);

            url_document ud = null;

            ud = new url_document();
            ud.url = resultDocument.getString("url");
            ud.sid = (resultDocument.get("sid") instanceof Integer) ? resultDocument.getInteger("sid") : resultDocument.getDouble("sid").intValue();
            ud.uid = resultDocument.getString("uid");
            ud._id = new ObjectId(resultDocument.get("_id").toString());
            ud.indexer_visited = (resultDocument.get("indexer_visited") instanceof Integer) ? resultDocument.getInteger("indexer_visited") : resultDocument.getDouble("indexer_visited").intValue();
            ud.crawler_visited = (resultDocument.get("crawler_visited") instanceof Integer) ? resultDocument.getInteger("crawler_visited") : resultDocument.getDouble("crawler_visited").intValue();

            return ud;
        }
    }


    public void add_total_words(String url, int words)
    {
        MongoCollection<Document> col = db.getCollection("urls");
        Bson filter = Filters.eq("uid", url);
        Bson updateOperation = Updates.set("total_words", words);
        col.updateOne(filter,updateOperation);
    }

    // Functions Of The Crawler =============================================================================================

    public boolean check_repeated_url(String url) // this function checks for repeted urls
    {
        MongoCollection<Document> col = db.getCollection("urls");
        Document D = new Document("url",url);
        return col.countDocuments(D) == 0;
    }

    public boolean check_repeated_uid(String uid) // This function checks for repeated web pages with different urls
    {
        MongoCollection<Document> col = db.getCollection("urls");
        Document D = new Document("uid",uid);
        return col.countDocuments(D) == 0;
    }

    public ArrayList<Document> get_previous_urls()
    {
        MongoCollection<Document> col = db.getCollection("urls");
        MongoCursor<Document> cursor = col.find().iterator();
        ArrayList<Document> documents = new ArrayList<>();
        try {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                documents.add(doc);
            }
        } finally {
            cursor.close();
        }
        return documents;
    }

    public int get_index_to_continou_crawling()
    {
        //start_server();
        MongoCollection<Document> col = db.getCollection("urls");
        Document D = new Document("crawler_visited",1);
        return col.find(D).iterator().available();
    }
    public url_document make_crawler_document(String url,String uid, int j)
    {
        url_document urlDocument = new url_document();
        urlDocument.url = url;
        urlDocument.uid = uid;
        // this is wrong i think
        urlDocument.sid = j;
        urlDocument._id = new ObjectId();
        urlDocument.crawler_visited = 0;
        urlDocument.indexer_visited = 0;
        urlDocument.urls_points_to_this_document = new ArrayList<>();

        return urlDocument;
    }

    public void update_crawler_visited(String url)
    {
        //start_server();
        MongoCollection<Document> col = db.getCollection("urls");
        Bson filter = Filters.eq("url", url);
        Bson updateOperation = Updates.set("crawler_visited", 1);
        // create an update operation to modify the document
        col.updateOne(filter, updateOperation);
    }

    public  void add_url1_to_url2List(String url1, String url2)
    {
        //start_server();

        // i changed url1 to url2 as you add to the second one why you modify the first
        Document query = new Document("url",url2);

        // Malek Output Change later if you need
        MongoCollection<Document> col = db.getCollection("urls");

        FindIterable<Document> ret = col.find(query);

        url_document ud = null;

        for (Document D : ret)
        {
            ud = new url_document();
            //System.out.println(D.toString());


            ud.url = D.getString("url");
            ud.sid = (D.get("sid") instanceof Integer) ? D.getInteger("sid") : D.getDouble("sid").intValue();
            ud.uid = D.getString("uid");
            ud._id = new ObjectId(D.get("_id").toString());
            ud.indexer_visited = (D.get("indexer_visited") instanceof Integer) ? D.getInteger("indexer_visited") : D.getDouble("indexer_visited").intValue();
            ud.crawler_visited = (D.get("crawler_visited") instanceof Integer) ? D.getInteger("crawler_visited") : D.getDouble("crawler_visited").intValue();
            ud.urls_points_to_this_document = (ArrayList<String>) D.get("urls_points_to_this_document");
        }

        if (ud == null)
            return;
        //System.out.println(ud.toString());

        if (ud.urls_points_to_this_document == null) ud.urls_points_to_this_document = new ArrayList<>();

        if(!ud.urls_points_to_this_document.contains(url1))
        {
            ud.urls_points_to_this_document.add(url1);
            Bson filter = Filters.eq("url", url2);
            col.deleteMany(filter);
            col.insertOne(class_to_document(ud));
        }
        //close_server();
    }

    public String get_next_document_to_visit()
    {

        Document query = new Document("crawler_visited",0);

        MongoCollection<Document> col = db.getCollection("urls");

        FindIterable<Document> ret = col.find(query).sort(Sorts.ascending("sid")).limit(1);

        // moa is edited here
        String url = null;

        for (Document D : ret)
        {
            url = D.getString("url");
        }

        update_crawler_visited(url);
        return url;
    }

    public int get_previous_urls_count()
    {
        //start_server();
        MongoCollection<Document> col = db.getCollection("urls");
        return (int)col.countDocuments();
    }


}
