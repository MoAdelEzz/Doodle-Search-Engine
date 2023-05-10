public class QueryResults {
    String url;
    String title;
    String paragraph;
    double priority;

    public QueryResults(String url, String title, String paragraph) {
        this.url = url;
        this.title = title;
        this.paragraph = paragraph;
    }
    public double getPriority() {
        return priority;
    }
}
