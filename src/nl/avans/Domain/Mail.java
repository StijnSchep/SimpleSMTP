package nl.avans.Domain;

import java.util.List;

public class Mail {
    private String from, body;
    private List<String> to;

    public Mail(String from, List<String> to, String body) {
        this.from = from;
        this.body = body;
        this.to = to;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FROM: ");
        sb.append(from);
        sb.append("\r\n");
        sb.append("TO: ");
        sb.append(to.get(0));
        sb.append("\r\n");

        for(int i = 1; i < to.size(); i++) {
            sb.append(" ");
            sb.append(to.get(i));
            sb.append("\r\n");
        }

        sb.append(body);
        return sb.toString();
    }
}
