package socialnetwork.repository.database;

import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class FriendshipRequestDbRepository  implements Repository<Tuple<Long, Long>, FriendshipRequest> {
    private String url;
    private String username;
    private String password;
    private Validator<FriendshipRequest> validator;
    Set<FriendshipRequest> requests = new HashSet<>();

    public FriendshipRequestDbRepository(String url, String username, String password, Validator<FriendshipRequest> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        loadData();
    }
    /**
     * Reads all the FriendshipRequests objects from database
     */
    private void loadData(){
        requests.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from requests");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String status=resultSet.getString("status");
                String date=resultSet.getString("request_date");

                FriendshipRequest friendshipRequest = new FriendshipRequest(new Tuple<>(id1,id2),status);
                friendshipRequest.setDate(LocalDateTime.parse(date));
                requests.add(friendshipRequest);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the FriendshipRequest with the given id
     * @param longLongTuple:the FriendshipRequests id
     * @return x:the FriendshipRequests with the given id
     * @throws ValidationException if the FriendshipRequests doesn't exist
     */

    @Override
    public FriendshipRequest findOne(Tuple<Long, Long> longLongTuple) {
        for (FriendshipRequest x:requests) {

           if(x.getId().equals(longLongTuple)) return x;

        }
        throw new ValidationException("This id doesn't exist!");
    }

    /**
     * Checks if an FriendshipRequests is in the database
     * @param longLongTuple-the id of the FriendshipRequests we are looking for
     * @return true, if it exists
     *         false,else
     */
    @Override
    public boolean exists(Tuple<Long, Long> longLongTuple) {
        for (FriendshipRequest x:requests) {
            if (x.getId().equals(longLongTuple))
            {
                System.out.println("exista: "+longLongTuple);
                return true;
            }

        }
        return false;
    }

    /**
     * Returns all the FriendshipRequests that are in the database
     * @return Iterable:FriendshipRequests
     */
    @Override
    public Iterable<FriendshipRequest> findAll() {
        loadData();
        return requests;
    }

    /**
     * Returns an ArrayList with all the FriendshipRequests in the database
     * @return ArrayList:FriendshipRequests
     */
    @Override
    public ArrayList<FriendshipRequest> getAll() {
        loadData();
        ArrayList<FriendshipRequest> arr =new ArrayList<>() ;
        arr.addAll(requests);
        return  arr;
    }

    /**
     * Saves a new FriendshipRequests object in the database, table:Friendship
     * @param entity
     *         entity must be not null
     * @return null if entity can be saved in the database
     * @throws  ValidationException if there is already an entity with the same id
     */
    @Override
    public FriendshipRequest save(FriendshipRequest entity) {
        if (entity == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        validator.validate(entity);
        for(FriendshipRequest x:requests) {
            if (x.getId().getLeft()==entity.getId().getLeft()&&x.getId().getRight()==entity.getId().getRight()) throw new ValidationException("friendship exist already!");

        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("connected...........");
            String sql="insert into requests values(?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1,(int)(long)entity.getId().getLeft());
            statement.setInt(2,(int)(long)entity.getId().getRight());
            statement.setString(3,entity.getStatus());
            statement.setString(4,entity.getDate().toString());

            int rows=statement.executeUpdate();
            if(rows>0){

                loadData();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method that deletes from the database the FriendshipRequest with the given id
     * @param longLongTuple
     *      id must be not null
     * @return null, if the FriendshipRequest has been deleted
     * @throws ValidationException if it doesn't exist
     */
    @Override
    public FriendshipRequest delete(Tuple<Long, Long> longLongTuple) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("connected...........");
            String sql="delete from requests where id1 = ? and id2 = ?";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1,(int)(long)longLongTuple.getLeft());
            statement.setInt(2,(int)(long)longLongTuple.getRight());

            int rows=statement.executeUpdate();
            if(rows>0){

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
    public FriendshipRequest update(FriendshipRequest entity) {
        return null;
    }
}
