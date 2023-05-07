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

        mongo.insert_into_db("urls", mongo.make_crawler_document("https://www.google.com/search?q=facebook&source=hp&ei=ocdXZPuiDvenkdUP_qKdmAo&iflsig=AOEireoAAAAAZFfVse4PVk-KPvuSUjbNZsK4sbu7YYvY&gs_ssp=eJzj4tLP1TfIyK1MKy5TYDRgdGDw4khLTE5Nys_PBgBmYAfL&oq=fac&gs_lcp=Cgdnd3Mtd2l6EAMYADIRCC4QgwEQxwEQsQMQ0QMQgAQyCwgAEIAEELEDEIMBMgsIABCABBCxAxCDATILCAAQgAQQsQMQgwEyCwgAEIAEELEDEIMBMgsIABCKBRCxAxCDATILCAAQgAQQsQMQgwEyCwgAEIAEELEDEIMBMgsIABCABBCxAxCDATILCAAQigUQsQMQgwE6EQguEIAEELEDEIMBEMcBENEDOgsILhCABBCxAxCDAToOCC4QgAQQsQMQgwEQ1AI6CwguEIoFELEDEIMBUEpY_QJgsAhoAXAAeACAAXqIAdUCkgEDMC4zmAEAoAEBsAEA&sclient=gws-wiz",
                "testing"
                , -8));

        mongo.close_server();


    }
}
