package app.queuena;

public class CustomMessage {
    private String message;
    private String time;

    public CustomMessage(String msg, String time) {
        this.message = msg;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }
}
