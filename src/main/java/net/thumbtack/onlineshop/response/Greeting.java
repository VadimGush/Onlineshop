package net.thumbtack.onlineshop.response;

public class Greeting {

    private final int id;
    private final String content;

    public Greeting(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getcontent() {
        return content;
    }
}
