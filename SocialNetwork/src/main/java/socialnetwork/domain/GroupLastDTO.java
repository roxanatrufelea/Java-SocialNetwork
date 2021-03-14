package socialnetwork.domain;

import java.time.LocalDateTime;

public class GroupLastDTO extends Entity<Long>{
    private String name;
    private User user;
    private String message;
    private LocalDateTime date;

    public GroupLastDTO(Long idi,String name, User user, String message, LocalDateTime date) {

        this.id=idi;
        this.name = name;
        this.user = user;
        this.message = message;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "GroupLastDTO{" +
                "name='" + name + '\'' +
                ", user=" + user +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
