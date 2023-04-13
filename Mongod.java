

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.IOException;
import java.lang.reflect.Field;

public class Mongod {

    String dbname = "search_engine";
    MongoClient client = null;
    MongoDatabase db = null;


    private void start_server()
    {
         client = MongoClients.create("mongodb://localhost:27017");

         db = client.getDatabase("test");
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
            // {id: 0 , Enc..:}
        }

        return D;
    }

    public void insert_into_db(String collection_name, Object obj)
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
}
class Human{
    String name;
    public Human(String name) {
        this.name = name;
    }
}