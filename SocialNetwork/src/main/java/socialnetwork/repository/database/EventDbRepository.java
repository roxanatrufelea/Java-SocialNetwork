package socialnetwork.repository.database;

import socialnetwork.domain.Event;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class EventDbRepository implements Repo<Long, Event> {
    protected String url;
    protected String username;
    protected String password;
    protected int pageSize = 7;
    private Validator<Event> validator;
    Set<Event> events = new HashSet<>();

    public EventDbRepository(String url, String username, String password, Validator<Event> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        loadData();
    }

    /**
     * method that reads all the data from the Events table in the database
     */
    public void loadData(){
        events.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from events");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String date = resultSet.getString("dateEvent");
                String time=resultSet.getString("timeEvent");
                String  participants=resultSet.getString("participants");
                List<Long> users_list=new ArrayList<>();
                if(!participants.isEmpty()) users_list= (List<Long>) Arrays.stream(participants.split(" ")).map(x->Long.parseLong(x)).collect(Collectors.toList());


                Long admin=resultSet.getLong("admin");
                String description=resultSet.getString("description");

                Event event=new Event(id,name, LocalDate.parse(date), LocalTime.parse(time),admin,users_list,description);
                events.add(event);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Saves a new Event object in the database, table:Events
     * @param entity
     *         entity must be not null
     * @return null if entity can be saved in the database
     * @throws  ValidationException if there is already an entity with the same id
     */
    @Override
    public Event save(Event entity) {
        if (entity == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        validator.validate(entity);
        for(Event x:events) {
            if (x.getId()==entity.getId())
                throw new ValidationException("id exists already!");
        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("connected...........");
            String sql="insert into events values(?,?,?,?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);
            String participants="";
            for (Long id:entity.getParticipants()) {
                participants += id.toString() + " ";
            }

            statement.setInt(1,(int)(long)entity.getId());
            statement.setString(2,entity.getName());
            statement.setString(3, entity.getDate().toString());
            statement.setString(4, entity.getStart().toString());
            statement.setString(5, participants);
            statement.setInt(6, (int)(long)entity.getAdmin());
            statement.setString(7, entity.getDescription());

            int rows=statement.executeUpdate();
            if(rows>0){
                //System.out.println("The users has been added..");
                loadData();

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }


    /**
     * Return the Event with the given id
     * @param aLong-the id of the event we want to get
     * @return Event: the event we are looking for
     * @throws ValidationException if there  is no event with that id
     */
    @Override
    public Event findOne(Long aLong) {
        for (Event x : events) {
            if (x.getId() == aLong)
                return x;

        }
        throw new ValidationException("id doesn't exist!");
    }

    /**
     * Checks if an event is in the database
     * @param aLong-the id of the event we are looking for
     * @return true, if it exists
     *         false,else
     */
    @Override
    public boolean exists(Long aLong) {
        for (Event x : events) {
            if (x.getId() == aLong)
                return true;

        }
        return false;
    }


    /**
     * Returns all the events that are in the database
     * @return Iterable:events
     */

    @Override
    public Iterable<Event> findAll() {
        return events;
    }


    /**
     * Returns an ArrayList with all the events in the database
     * @return ArrayList:events
     */
    @Override
    public ArrayList<Event> getAll() {
        ArrayList<Event> arr = new ArrayList<>();
        arr.addAll(events);
        return arr;
    }


    /**
     * Method that deletes from the database the event with the given id
     * @param id
     *      id must be not null
     * @return null, if the event has been deleted
     * @throws ValidationException if it doesn't exist
     */

    @Override
    public Event delete(Long id) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("connected...........");
            String sql = "delete from events where id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, (int) (long) id);


            int rows = statement.executeUpdate();
            if (rows > 0) {
                //System.out.println("The user has been deleted..");
                loadData();

            } else {
                throw new ValidationException("this id doesn't exist!");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public Event update(Event entity) {
        return null;
    }


    /**
     * Returns the number of pages of Events that are in the database
     * @param pageNumber: number of pages
     * @return events
     */
    @Override
    public Iterable<Event> getPage(int pageNumber) {
        return events.stream()
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

    }

    /**
     * Returns ta page of events of a user with the given id that are in the database
     * @param pageNumber: the page number
     * @param id: the user id
     * @return events
     */
    @Override
    public Iterable<Event> getPageUser(int pageNumber, Long id) {
        return events.stream()
                .filter(x->x.getParticipants().contains(id))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }


    /**
     * Returns the number of pages of events in the database
     * @return number of pages
     */
    @Override
    public int getPageCount() {
        return events.size() / pageSize + 1;
    }

    /**
     * Returns the number of pages of events in the database of a user with the given id
     * @param id:user id
     * @return number of pages
     */
    @Override
    public int getPageCountUser(Long id) {
        return (int) (events.stream()
                        .filter(x->x.getParticipants().contains(id))
                        .count()/pageSize+1);
    }
}
