package socialnetwork.repository.database;

import socialnetwork.domain.Message;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repo;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class MessageDbRepository implements Repo<Long, Message> {

    private String url;
    private String username;
    private String password;
    private Validator<Message> validator;
    Set<Message> messages = new HashSet<>();
    protected int pageSize = 7;

    public MessageDbRepository(String url, String username, String password, Validator<Message> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        loadData();
    }

    /**
     * Returns the message with the given id
     * @param aLong:the message id
     * @return x:the message with the given id
     * @throws ValidationException if the message doesn't exist
     */
    @Override
    public Message findOne(Long aLong) {
        for(Message x:messages) {
            if (x.getId().equals(aLong))
                return x;

        }

        throw new ValidationException("id doesn't exist!");
    }

    /**
     * Checks if an message is in the database
     * @param aLong-the id of the message we are looking for
     * @return true, if it exists
     *         false,else
     */
    @Override
    public boolean exists(Long aLong) {
        for(Message x:messages) {
            if (x.getId().equals(aLong))
                return true;

        }
        return false;
    }

    /**
     * Reads all the message objects from database
     */
    public void loadData(){
        messages.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from messages");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long sender=resultSet.getLong("sender");
                String receivers = resultSet.getString("receivers");
                String text = resultSet.getString("text");
                String date =resultSet.getString("date");
                String reply=resultSet.getString("reply");

                List<Long> receive_list= (ArrayList<Long>) Arrays.stream(receivers.split(" ")).map(x->Long.parseLong(x)).collect(Collectors.toList());
                Message message = new Message(id,sender,receive_list,text, LocalDateTime.parse(date));
                message.setReply(reply);
                messages.add(message);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Returns all the messages that are in the database
     * @return Iterable:messages
     */
    @Override
    public Iterable<Message> findAll() {

        return messages;

    }

    /**
     * Returns an ArrayList with all the messages in the database
     * @return ArrayList:messages
     */
    @Override
    public ArrayList<Message> getAll() {
        ArrayList<Message> arr=new ArrayList<>();
        arr.addAll(messages);
        return arr;
    }
    /**
     * Saves a new message object in the database, table:message
     * @param entity
     *         entity must be not null
     * @return null if entity can be saved in the database
     * @throws  ValidationException if there is already an entity with the same id
     */
    @Override
    public Message save(Message entity) {
        if (entity == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        validator.validate(entity);
        for(Message x:messages) {
            if (x.getId()==entity.getId())
                throw new ValidationException("id exists already!");
        }
        String receivers="";
        for (Long id:entity.getTo()) {
            receivers+=id.toString()+" ";

        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("connected...........");
            String sql="insert into messages values(?,?,?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1,(int)(long)entity.getId());
            statement.setInt(2,(int)(long)entity.getFrom());
            statement.setString(3,receivers);
            statement.setString(4, entity.getMessage());
            statement.setString(5, entity.getDate().toString());
            statement.setString(6, entity.getReply());

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
     * Method that deletes from the database the message with the given id
     * @param id
     *      id must be not null
     * @return null, if the message has been deleted
     * @throws ValidationException if it doesn't exist
     */
    @Override
    public Message delete(Long id) {

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("connected...........");
            String sql="delete from messages where id = ?";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1,(int)(long)id);


            int rows=statement.executeUpdate();
            if(rows>0){
                //System.out.println("The user has been deleted..");
                loadData();

            }else{
                throw new ValidationException("this id doesn't exist!");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }
    /**
     * Returns the number of pages of messages that are in the database
     * @param pageNumber: number of pages
     * @return messages
     */
    @Override
    public Iterable<Message> getPage(int pageNumber) {
        return messages.stream().
                skip(pageNumber * pageSize).
                limit(pageSize).
                collect(Collectors.toList());

    }

    /**
     * Returns ta page of messages of a user with the given id that are in the database
     * @param pageNumber: the page number
     * @param id: the user id
     * @return Groups
     */
    public Iterable<Message> getPageUser(int pageNumber,Long id) {
        return messages.stream()
                .filter(x->(x.getFrom().equals(id)||x.getTo().contains(id)))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

    }

    /**
     * Returns the number of pages of messages in the database
     * @return number of pages
     */
    @Override
    public int getPageCount() {
        return messages.size() / pageSize + 1;
    }

    /**
     * Returns the number of pages of messages in the database of a user with the given id
     * @param id:user id
     * @return number of pages
     */
    @Override
    public int getPageCountUser(Long id) {
        return (int) (messages.stream()
                        .filter(x->(x.getFrom().equals(id)||x.getTo().contains(id)))
                        .count()/pageSize +1);
    }
}
