package socialnetwork.domain;

import java.time.LocalDateTime;

public class MessageDTO extends Entity<Long>{

    private String message;
    private String reply=null;
    private LocalDateTime date;
    private User from;

    public MessageDTO(Long idi,User from, String message,  LocalDateTime date) {
        this.id=idi;
        this.from = from;
        this.message = message;
        this.date = date;
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "from=" + from +
                ", message='" + message + '\'' +
                ", reply='" + reply + '\'' +
                ", date=" + date +
                '}';
    }
}
