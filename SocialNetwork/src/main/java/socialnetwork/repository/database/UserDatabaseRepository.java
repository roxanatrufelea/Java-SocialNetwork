package socialnetwork.repository.database;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserDatabaseRepository implements Repo<Long, User> {


    protected String url;
    protected String username;
    protected String password;
    protected int pageSize = 7;
    Set<User> users = new HashSet<>();

    private Validator<User> validator;



    public UserDatabaseRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        loadData();
    }
/*
reads all the users from the database
 */
    public void loadData(){
        users.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from users");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String email=resultSet.getString("email");
                String password=resultSet.getString("password");
                String gender=resultSet.getString("gender");
                String date=resultSet.getString("birthDate");
                String notifications= resultSet.getString("notifications");


                User user = new User(firstName, lastName,email,gender,password, LocalDate.parse(date));
                user.setId(id);
                user.setNotifications(notifications);
                users.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves a new User object in the database, table:User
     * @param entity
     *         entity must be not null
     * @return null if entity can be saved in the database
     * @throws  ValidationException if there is already an entity with the same id
     */
    @Override
    public User save(User entity) {
        if (entity == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        validator.validate(entity);
        for(User x:users) {
            if (x.getId()==entity.getId())
                throw new ValidationException("id exists already!");
        }
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("connected...........");
            String sql="insert into users values(?,?,?,?,?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1,(int)(long)entity.getId());
            statement.setString(2,entity.getFirstName());
            statement.setString(3, entity.getLastName());
            statement.setString(4, entity.getEmail());
            statement.setString(5, entity.getPassword());
            statement.setString(6, entity.getBirthDate().toString());
            statement.setString(7, entity.getGender());
            statement.setString(8, entity.getNotifications());

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
     * Returns the User with the given id
     * @param aLong:the User id
     * @return x:the User with the given id
     * @throws ValidationException if the User doesn't exist
     */
    @Override
    public User findOne(Long aLong) {
        for (User x : users) {
            if (x.getId() == aLong)
                return x;

        }
        throw new ValidationException("id doesn't exist!");
    }

    /**
     * Checks if an User is in the database
     * @param aLong-the id of the User we are looking for
     * @return true, if it exists
     *         false,else
     */
    @Override
    public boolean exists(Long aLong) {
        for (User x : users) {
            if (x.getId() == aLong)
                return true;

        }
        return false;
    }
    /**
     * Returns all the Users that are in the database
     * @return Iterable:Users
     */
    @Override
    public Iterable<User> findAll() {

        return users;

    }
    /**
     * Returns an ArrayList with all the Users in the database
     * @return ArrayList:Users
     */
    @Override
    public ArrayList<User> getAll() {
        ArrayList<User> arr = new ArrayList<>();
        arr.addAll(users);
        return arr;
    }
    /**
     * Method that deletes from the database the User with the given id
     * @param id
     *      id must be not null
     * @return null, if the User has been deleted
     * @throws ValidationException if it doesn't exist
     */
    @Override
    public User delete(Long id) {

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("connected...........");
            String sql = "delete from users where id = ?";

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
    /**
     * Returns the number of pages of Users in the database of a user with the given id
     * @param id:user id
     * @return number of pages
     */
    @Override
    public int getPageCountUser(Long id) {
        return users.size() / pageSize + 1;
    }

    @Override
    public User update(User entity) {
        return null;
    }
    /**
     * Returns the number of pages of Users that are in the database
     * @param pageNumber: number of pages
     * @return Users
     */
    @Override
    public Iterable<User> getPage(int pageNumber) {
        return users.stream().skip(pageNumber * pageSize).limit(pageSize).collect(Collectors.toList());

    }
    /**
     * Returns ta page of Users of a user with the given id that are in the database
     * @param pageNumber: the page number
     * @param id: the user id
     * @return Users
     */
    @Override
    public Iterable<User> getPageUser(int pageNumber, Long id) {
        return users.stream()
                .filter(x->x.getId().equals(id))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

    }

    /**
     * Returns the number of pages of Users in the database
     * @return number of pages
     */
    @Override
    public int getPageCount() {
        return users.size() / pageSize + 1;
    }


}




