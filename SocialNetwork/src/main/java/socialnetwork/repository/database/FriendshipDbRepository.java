package socialnetwork.repository.database;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repo;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FriendshipDbRepository implements Repo<Tuple<Long, Long>, Friendship> {
    private String url;
    private String username;
    private String password;
    private Validator<Friendship> validator;
    Set<Friendship> friends = new HashSet<>();
    protected int pageSize = 7;

    public FriendshipDbRepository(String url, String username, String password, FriendshipValidator validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        loadData();
    }

    /**
     * Reads all the Friendship objects from database
     */
    private void loadData(){
        friends.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from friendships");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id1 = resultSet.getLong("id1");
                Long id2 = resultSet.getLong("id2");
                String date=resultSet.getString("f_date");

                Friendship friendship = new Friendship(new Tuple<>(id1,id2));
                friendship.setDate(LocalDateTime.parse(date));
                friends.add(friendship);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the Friendship with the given id
     * @param longLongTuple:the friendship id
     * @return x:the friendship with the given id
     * @throws ValidationException if the friendship doesn't exist
     */
    @Override
    public Friendship findOne(Tuple<Long, Long> longLongTuple) {
        for (Friendship x:friends) {
            if (x.getId().equals(longLongTuple)) return x;

        }
        throw new ValidationException("This id doesn't exist!");
    }

    /**
     * Checks if an friendship is in the database
     * @param longLongTuple-the id of the friendship we are looking for
     * @return true, if it exists
     *         false,else
     */
    @Override
    public boolean exists(Tuple<Long, Long> longLongTuple) {
        for (Friendship x:friends) {
            if (x.getId().equals(longLongTuple)) return true;

        }
        return false;
    }

    /**
     * Returns all the friendships that are in the database
     * @return Iterable:friendships
     */
    @Override
    public Iterable<Friendship> findAll() {
        loadData();
        return friends;
    }

    /**
     * Returns an ArrayList with all the friendships in the database
     * @return ArrayList:friendships
     */
    @Override
    public ArrayList<Friendship> getAll() {
        loadData();
        ArrayList<Friendship> arr =new ArrayList<>() ;
        arr.addAll(friends);
        return  arr;
    }


    /**
     * Saves a new Friendship object in the database, table:Friendship
     * @param entity
     *         entity must be not null
     * @return null if entity can be saved in the database
     * @throws  ValidationException if there is already an entity with the same id
     */
    @Override
    public Friendship save(Friendship entity) {
        if (entity == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        validator.validate(entity);
        for(Friendship x:friends) {
            if (x.getId().getLeft()==entity.getId().getLeft()&&x.getId().getRight()==entity.getId().getRight()) throw new ValidationException("friendship exist already!");

        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("connected...........");
            String sql="insert into friendships values(?,?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1,(int)(long)entity.getId().getLeft());
            statement.setInt(2,(int)(long)entity.getId().getRight());
            statement.setString(3,entity.getDate().toString());

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
     * Method that deletes from the database the friendship with the given id
     * @param longLongTuple
     *      id must be not null
     * @return null, if the friendship has been deleted
     * @throws ValidationException if it doesn't exist
     */
    @Override
    public Friendship delete(Tuple<Long, Long> longLongTuple) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("connected...........");
            System.out.println(longLongTuple);
            String sql="delete from friendships where id1 = ? and id2 = ?";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1,(int)(long)longLongTuple.getLeft());
            statement.setInt(2,(int)(long)longLongTuple.getRight());

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
    public Friendship update(Friendship entity) {
        return null;
    }


    /**
     * Returns the number of pages of friendships that are in the database
     * @param pageNumber: number of pages
     * @return friendships
     */
    @Override
    public Iterable<Friendship> getPage(int pageNumber) {
        return friends.stream()
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

    }

    /**
     * Returns ta page of friendships of a user with the given id that are in the database
     * @param pageNumber: the page number
     * @param id: the user id
     * @return friendships
     */
    @Override
    public Iterable<Friendship> getPageUser(int pageNumber, Long id) {
        return friends.stream()
                .filter(x->x.getId().getLeft().equals(id)||x.getId().getRight().equals(id))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

    }


    /**
     * Returns the number of pages of friendships in the database
     * @return number of pages
     */

    @Override
    public int getPageCount() {
        return friends.size() / pageSize + 1;
    }

    /**
     * Returns the number of pages of friendships in the database of a user with the given id
     * @param id:user id
     * @return number of pages
     */
    @Override
    public int getPageCountUser(Long id) {
        return (int) (friends.stream()
                .filter(x->x.getId().getLeft().equals(id)||x.getId().getRight().equals(id))
                .count()/pageSize+1);
    }
}

