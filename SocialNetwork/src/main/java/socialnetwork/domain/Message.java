package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {

    private Long from;
    private List<Long> to;
    private String message;
    private LocalDateTime date;
    private String reply=null;

    public Message(Long idi, Long from, List<Long> to, String message, LocalDateTime date) {
        id=idi;
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;

    }

    /**
     * Returns a string =reply
     * @return reply
     */
    public String getReply() {
        return reply;
    }

    /**
     * Sets the reply
     * @param reply
     */
    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * Returns a long, the user id that sends a message
     * @return from
     */
    public Long getFrom() {
        return from;
    }

    /**
     * Returns a list of ids , the receivers id
     * @return
     */
    public List<Long> getTo() {
        return to;
    }

    /**
     * Returns a string, the text
     * @return
     */
    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }



    public void setFrom(Long from) {
        this.from = from;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(id, message1.id) &&
                Objects.equals(from, message1.from) &&
                Objects.equals(to, message1.to) &&
                Objects.equals(message, message1.message) &&
                Objects.equals(date, message1.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, from, to, message, date);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id="+id+
                ", from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", reply='" + reply + '\'' +
                '}';
    }
}
