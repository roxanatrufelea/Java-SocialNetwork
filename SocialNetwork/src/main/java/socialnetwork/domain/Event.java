package socialnetwork.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Event extends Entity<Long>{
    private String name;
    private LocalDate date;
    private LocalTime start;

    private Long admin;
    private List<Long> participants;
    private String description;

    public Event(Long id,String name, LocalDate date, LocalTime start,Long admin, List<Long> participants,String description) {
        this.id=id;
        this.name = name;
        this.date = date;
        this.start = start;
        this.admin = admin;
        this.participants = participants;
        this.description=description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;

    }

    public Long getAdmin() {
        return admin;
    }

    public void setAdmin(Long admin) {
        this.admin = admin;
    }

    public List<Long> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Long> participants) {
        this.participants = participants;
    }
    public void addParticipant(Long id)
    {
        this.participants.add(id);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return name +" "+date +" "+start;
    }
}
