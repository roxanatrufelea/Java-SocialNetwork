package socialnetwork.service;

import socialnetwork.domain.Event;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repo;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventsService implements Observable<UserChangeEvent> {
    private Repo<Long, User> repoUser;
    private Repo<Long, Event> repoEvent;
    private Long contor=0l;

    public EventsService(Repo<Long, User> repoUser, Repo<Long, Event> repoEvent) {
        this.repoUser = repoUser;
        this.repoEvent = repoEvent;
        contor=maxContor();
    }

    private Long maxContor() {
        Long m = 0l;
        if (repoEvent.getAll().size() > 0) {
            m = repoEvent.getAll().stream()
                    .map(x -> x.getId())
                    .max(Long::compare).get();
        }
        return m;

    }

    private List<Observer<UserChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<UserChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<UserChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(UserChangeEvent t) {
        observers.stream().forEach(x -> x.update(t));
    }


    /**
     * Creates and adds an event in the database
     * @param name: event name
     * @param date: date of the event
     * @param time: time of the event
     * @param admin: the user that creates the event
     * @param description: the description of the event
     */
    public void addEvent(String name, LocalDate date, LocalTime time, Long admin,String description) {
        if(date.compareTo(LocalDate.now())<0||(date.equals(LocalDate.now()) && time.compareTo(LocalTime.now())<0))throw new ValidationException("invalid date-time!");

        List<Long> participants=new ArrayList<>();
        participants.add(admin);
        contor++;
        Event task = new Event(contor,name,date,time,admin,participants,description);
        repoEvent.save(task);
        notifyObservers(new UserChangeEvent(ChangeEventType.ADD, null));


    }

    /**
     * Deletes the event with the id: idEvent, only if it was created by the user wiyh id: id_user
     * @param idEvent: event id
     * @param id_user: user id
     * @throws ValidationException: if the user with idEvent is not the admin of the event
     */
    public void deleteEvent(Long idEvent,Long id_user)
    {
        if(!repoEvent.findOne(idEvent).getAdmin().equals(id_user)) throw new ValidationException("You are not the admin!You can't delete this event");
        repoEvent.delete(idEvent);
        notifyObservers(new UserChangeEvent(ChangeEventType.DELETE, null));

    }

    /**
     * Adds the user  with the given id: id_user to the list of participants of the event
     * @param event: the given event
     * @param id_user: the id user
     * @throws ValidationException
     */
    public void joinEvent(Event event,Long id_user)
    {
        if (event.getParticipants().contains(id_user) ) throw new ValidationException("You have already joined this event");
        if(event.getDate().compareTo(LocalDate.now())<0)throw new ValidationException("This event has finished");
        repoEvent.delete(event.getId());
        List<Long> participants=event.getParticipants();
        participants.add(id_user);
        Event updated=new Event(event.getId(), event.getName(),event.getDate(),event.getStart(),event.getAdmin(),participants, event.getDescription());
        repoEvent.save(updated);
        notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE, null));

    }

    /**
     * Unjoin the event with the id: id_user: deletes the id_user from the list of participants of the event
     * @param event: given event
     * @param id_user: the id of the user
     * @throws ValidationException
     */

    public void unjoinEvent(Event event,Long id_user){
        if(event.getAdmin().equals(id_user)) throw new ValidationException("You created the event!");
        if (!event.getParticipants().contains(id_user) ) throw new ValidationException("You didn't join this event");
        if(event.getDate().compareTo(LocalDate.now())<0)throw new ValidationException("This event has finished");
        repoEvent.delete(event.getId());
        List<Long> participants=event.getParticipants();
        participants.remove(id_user);
        Event updated=new Event(event.getId(), event.getName(),event.getDate(),event.getStart(),event.getAdmin(),participants, event.getDescription());
        repoEvent.save(updated);
        notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE, null));

    }

    /**
     * Returns an ArrayList with all the coming events
     * @return events
     */
    public ArrayList<Event>getAllEvents()
    {
        return (ArrayList<Event>) repoEvent.getAll().stream()
                .filter(x->(x.getDate().compareTo(LocalDate.now())==0&&x.getStart().compareTo(LocalTime.now())>0)||(x.getDate().compareTo(LocalDate.now())>0))
                .collect(Collectors.toList());

    }

    /**
     * Returns the list of the events of today of the user with the given id
     * @param user_id: id of the user
     * @return events of today
     */
    public ArrayList<Event>getMyEventsToday(Long user_id)
    {
        return (ArrayList<Event>) repoEvent.getAll().stream()
                .filter(x->x.getDate().isEqual(LocalDate.now()))
                .filter(x->x.getStart().compareTo(LocalTime.now())>0)
                .filter(x->x.getParticipants().contains(user_id))
                .collect(Collectors.toList());

    }

    /**
     * Returns the list of events to which the user with the given id has joined
     * @param user_id: the id of the user
     * @return events
     */
    public ArrayList<Event>getGoingEvents(Long user_id)
    {
        return (ArrayList<Event>) repoEvent.getAll().stream()
                .filter(x->(x.getDate().compareTo(LocalDate.now())==0&&x.getStart().compareTo(LocalTime.now())>0)||(x.getDate().compareTo(LocalDate.now())>0))
                .filter(x->x.getParticipants().contains(user_id))
                .collect(Collectors.toList());

    }

    /**
     * Returns all the events the user with the given id has created
     * @param id_user: id of the uset
     * @return events
     */
    public ArrayList<Event>myEvents(Long id_user)
    {
        return (ArrayList<Event>) repoEvent.getAll().stream()
                .filter(x->x.getAdmin().equals(id_user))
                .collect(Collectors.toList());

    }

}
