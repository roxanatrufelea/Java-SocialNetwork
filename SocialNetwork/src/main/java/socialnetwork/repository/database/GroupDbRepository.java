package socialnetwork.repository.database;

import socialnetwork.domain.Group;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupDbRepository implements Repo<Long, Group> {
    private String url;
    private String username;
    private String password;
    private Validator<Group> validator;
    Set<Group> groups = new HashSet<>();
    protected int pageSize = 7;

    public GroupDbRepository(String url, String username, String password, Validator<Group> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        loadData();
    }

    /**
     * Reads all the Group objects from database
     */
    public void loadData(){
        groups.clear();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from groups");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name=resultSet.getString("nameGroup");
                String users = resultSet.getString("users");
                String messages = resultSet.getString("messages");

                ArrayList<Long> users_list= (ArrayList<Long>) Arrays.stream(users.split(" ")).map(x->Long.parseLong(x)).collect(Collectors.toList());
                ArrayList<Long> messages_list=new ArrayList<>();
                if(messages.length()>0) {
                    System.out.println("merge");
                    messages_list = (ArrayList<Long>) Arrays.stream(messages.split(" ")).map(x -> Long.parseLong(x)).collect(Collectors.toList());
                }
                Group group =new Group(id,name,users_list,messages_list);
                groups.add(group);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Saves a new Group object in the database, table:Grous
     * @param aLong
     *         entity must be not null
     * @return null if entity can be saved in the database
     * @throws  ValidationException if there is already an entity with the same id
     */
    @Override
    public Group findOne(Long aLong) {
        for(Group x:groups) {
            if (x.getId().equals(aLong))
                return x;

        }
        throw new ValidationException("id group doesn't exist!");
    }
    /**
     * Checks if an Group is in the database
     * @param aLong-the id of the Grou we are looking for
     * @return true, if it exists
     *         false,else
     */
    @Override
    public boolean exists(Long aLong) {
        for(Group x:groups) {
            if (x.getId().equals(aLong))
                return true;

        }
        return false;
    }

    /**
     * Returns all the Groups that are in the database
     * @return Iterable:Groups
     */
    @Override
    public Iterable<Group> findAll() {
        return groups;
    }

    /**
     * Returns an ArrayList with all the Groups in the database
     * @return ArrayList:Groups
     */
    @Override
    public ArrayList<Group> getAll() {
        ArrayList<Group> arr=new ArrayList<>();
        arr.addAll(groups);
        return arr;
    }
    /**
     * Saves a new Group object in the database, table:Group
     * @param entity
     *         entity must be not null
     * @return null if entity can be saved in the database
     * @throws  ValidationException if there is already an entity with the same id
     */

    @Override
    public Group save(Group entity) {
        if (entity == null) {
            throw new IllegalArgumentException("id group must not be null");
        }
        System.out.println(entity);
        validator.validate(entity);
        for(Group x:groups) {
            if (x.getId()==entity.getId())
                throw new ValidationException("id group exists already!");
        }
        String users="";
        for (Long id:entity.getUsers()) {
            users+=id.toString()+" ";

        }
        String messages="";
        for (Long id:entity.getMessages()) {
            messages+=id.toString()+" ";

        }
        System.out.println(entity.getId());
        System.out.println(entity.getName());
        System.out.println(users);
        System.out.println(messages);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("connected...........");
            String sql="insert into groups values(?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1,(int)(long)entity.getId());
            statement.setString(2, entity.getName());
            statement.setString(3, users);
            statement.setString(4, messages);


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
     * Method that deletes from the database the Group with the given id
     * @param id
     *      id must be not null
     * @return null, if the Group has been deleted
     * @throws ValidationException if it doesn't exist
     */
    @Override
    public Group delete(Long id) {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("connected...........");
            String sql="delete from groups where id = ?";

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
    public Group update(Group entity) {
        return null;
    }

    /**
     * Returns the number of pages of Groups that are in the database
     * @param pageNumber: number of pages
     * @return Groups
     */
    @Override
    public Iterable<Group> getPage(int pageNumber) {
        return groups.stream()
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

    }

    /**
     * Returns ta page of Groups of a user with the given id that are in the database
     * @param pageNumber: the page number
     * @param id: the user id
     * @return Groups
     */
    @Override
    public Iterable<Group> getPageUser(int pageNumber, Long id) {
        return groups.stream()
                .filter(x->x.getUsers().contains(id))
                .skip(pageNumber * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

    }

    /**
     * Returns the number of pages of Groups in the database
     * @return number of pages
     */
    @Override
    public int getPageCount() {
        return groups.size() / pageSize + 1;
    }

    /**
     * Returns the number of pages of Groups in the database of a user with the given id
     * @param id:user id
     * @return number of pages
     */
    @Override
    public int getPageCountUser(Long id) {
        return (int) (groups.stream()
                        .filter(x->x.getUsers().contains(id))
                        .count()/pageSize+1);
    }
}
