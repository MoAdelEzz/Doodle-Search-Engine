

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.lang.reflect.Field;

public class Mongod {

    String dbname = "search_engine";
    MongoClient client = null;
    MongoDatabase db = null;

    private void start_server()
    {
        MongoClient client = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase db = client.getDatabase("test");
    }

    private void close_server()
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
        }

        return D;
    }

    public void insert_into_db(String collection_name, Object obj)
    {
        start_server();
        Document D = class_to_document(obj);
        MongoCollection col = db.getCollection(collection_name);
        col.insertOne(D);
        close_server();
    }
}