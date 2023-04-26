import org.bson.types.ObjectId;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

class url_document {
    String uid; // generated encode
    String url; // url
    ObjectId _id; // mongo id
    int indexer_visited; // indexer flag
    int sid; // to make it bfs auto incremental
    int crawler_visited; // crawler flag

    ArrayList<String> urls_points_to_this_document;
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


class Encrypter {
    public static String encrypt(String plaintext) throws NoSuchAlgorithmException {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(plaintext.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }
}

class Pair<K, V> {

    private final K element0;
    private final V element1;

    public static <K, V> Pair<K, V> createPair(K element0, V element1) {
        return new Pair<K, V>(element0, element1);
    }

    public Pair(K element0, V element1) {
        this.element0 = element0;
        this.element1 = element1;
    }

    public K getElement0() {
        return element0;
    }

    public V getElement1() {
        return element1;
    }

}