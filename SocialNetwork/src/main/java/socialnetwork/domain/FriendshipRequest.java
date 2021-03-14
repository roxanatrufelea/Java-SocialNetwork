package socialnetwork.domain;

import java.time.LocalDateTime;

public class FriendshipRequest extends Entity<Tuple<Long,Long>>{
    private String status;
    LocalDateTime date=null;
    public FriendshipRequest() {

    }


    public FriendshipRequest(Tuple<Long,Long> t,String status) {
        id =t;
        this.status=status;
    }
    public FriendshipRequest(Tuple<Long,Long> t,String status,LocalDateTime date) {
        id =t;
        this.status=status;
        this.date=date;
    }

    /**
     * Returns a string, the status
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status as the given string
     * @param status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FriendshipRequest{ " +
                id.getLeft()+" | "+id.getRight()+" | "+ status  +
                " }";
    }
    public LocalDateTime getDate() {
        return date;
    }
    public String getDateString() {
        return date.toString();
    }
    public void setDate(LocalDateTime dt) {
        date= dt;
    }
}
