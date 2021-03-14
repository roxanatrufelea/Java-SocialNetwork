package socialnetwork.domain;

import java.time.LocalDateTime;


public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;

    public Friendship() {

    }

    public Friendship(Tuple<Long,Long> t) {
        id =t;
    }

    public Friendship(Tuple<Long,Long> t,LocalDateTime dt) {
        id =t;
        date=dt;
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime dt) {
        date= dt;
    }
}
