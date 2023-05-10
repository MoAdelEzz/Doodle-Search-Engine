import java.util.ArrayList;

public class WordData{
    String word;
    Double prio;
    ArrayList<Integer> tags;

    public WordData(String word, Double prio, ArrayList<Integer> tags) {
        this.word = word;
        this.prio = prio;
        this.tags = tags;
    }
}