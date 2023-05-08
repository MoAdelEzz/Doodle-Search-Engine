import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import opennlp.tools.stemmer.*;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class testing {
    static public boolean isParsableAsInt(String input) {
        try {
            Long.parseLong(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void main(String[] args) throws Exception {
        Mongod mongo = new Mongod();
        mongo.start_server();



        mongo.close_server();


    }
}
