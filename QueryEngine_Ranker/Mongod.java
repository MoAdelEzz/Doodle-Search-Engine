

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;
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



    public void start_server()
    {
        Logger logger = Logger.getLogger("org.mongodb.driver");
        logger.setLevel(Level.SEVERE);

        client = MongoClients.create("mongodb://localhost:27017");

        db = client.getDatabase("test");

    }

    public void close_server()
    {
        client.close();
    }




}
