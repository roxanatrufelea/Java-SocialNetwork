package socialnetwork.domain;

import java.time.LocalDateTime;

public class Activity {
    private String activity;
    private LocalDateTime date;

    public Activity(String activity, LocalDateTime date) {
        this.activity = activity;
        this.date = date;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final String s = "[" + date + "]: " + activity;
        return s;
    }
}
