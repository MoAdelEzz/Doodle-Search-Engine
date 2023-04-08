

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
public class Mongod {

    public static void main(String[] args) {

            MongoClient client = MongoClients.create("mongodb://localhost:27017");

            MongoDatabase db = client.getDatabase("test");

            MongoCollection col = db.getCollection("humans");

            Document sampleDoc = new Document("_id", "1").append("name", "John Smith");

            col.insertOne(sampleDoc);

            client.close();


    }
}