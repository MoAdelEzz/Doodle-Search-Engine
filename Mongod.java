

import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.*;

public class Mongod {

    String lock = "";
    String dbname = "search_engine";
    MongoClient client = null;
    MongoDatabase db = null;

    ArrayList<String> mongooooooooooo;


    public void start_server()
    {
        Logger logger = Logger.getLogger("org.mongodb.driver");
        logger.setLevel(Level.SEVERE);

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
        synchronized(this) {
            Document D = class_to_document(obj);
            MongoCollection col = db.getCollection(collection_name);
            col.insertOne(D);
        }
    }
    public void main(String[] args) throws IOException, IllegalAccessException {
        Mongod mongo = new Mongod();
        mongo.start_server();
        Human hima = new Human("hima");
        mongo.insert_into_db("humans",hima);
        return;
    }


    public url_document get_indexer_filter_input()
    {
        synchronized (this) {
            //start_server();

            Document query = new Document("indexer_visited", 0);
            Document sortby = new Document("defined_id", 1);

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

            Document q1 = new Document("_id", O);
            Document q2 = new Document("$set", new Document("indexer_visited", 1));

            col.updateOne(q1, q2);

            url_document ud = null;

            ud = new url_document();
            ud.url = resultDocument.getString("url");
            ud.sid = resultDocument.getDouble("sid").intValue();
            ud.uid = resultDocument.getString("uid");
            ud._id = resultDocument.get("_id").toString();
            ud.indexer_visited = resultDocument.getDouble("indexer_visited").intValue();
            ud.crawler_visited = resultDocument.getDouble("crawler_visited").intValue();

            return ud;
        }
    }

}
