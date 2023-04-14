

import com.mongodb.client.*;
import org.bson.Document;

import java.io.IOException;
import java.lang.reflect.Field;

public class Mongod {

    String dbname = "search_engine";
    static MongoClient client = null;
    static MongoDatabase db = null;


    private static void start_server()
    {
         client = MongoClients.create("mongodb://localhost:27017");

         db = client.getDatabase("test");
    }

    private static void close_server()
    {
        client.close();
    }

    private static Document class_to_document(Object obj)
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

    public static void insert_into_db(String collection_name, Object obj)
    {
        start_server();
        Document D = class_to_document(obj);
        MongoCollection col = db.getCollection(collection_name);
        col.insertOne(D);
    }
    public static void main(String[] args) throws IOException, IllegalAccessException {
        Mongod mongo = new Mongod();
        mongo.start_server();
        Human hima = new Human("hima");
        mongo.insert_into_db("humans",hima);
        return;
    }


    public static url_document get_indexer_filter_input()
    {
        start_server();

        Document query = new Document("indexer_visited",0);
        Document sortby = new Document("defined_id",1);

        // Malek Output Change later if you need
        MongoCollection col = db.getCollection("urls");

        FindIterable<Document> ret = col.find(query).sort(sortby).limit(1);

        url_document ud = new url_document();

        for (Document D : ret)
        {
            ud.url = D.getString("url");
            ud.sid = D.getDouble("sid").intValue();
            ud.uid = D.getString("uid");
            ud._id = D.get("_id").toString();
            ud.indexer_visited = D.getDouble("indexer_visited").intValue();
            ud.crawler_visited = D.getDouble("crawler_visited").intValue();
        }

        close_server();

        return ud;
    }

}
class Human{
    String name;
    public Human(String name) {
        this.name = name;
    }
}