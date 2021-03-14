package socialnetwork.service;

import socialnetwork.domain.Activity;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repo;
import socialnetwork.utils.Graph;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserService  implements Observable<UserChangeEvent> {

    private Repo<Long, User> repo;
    Repo<Tuple<Long, Long>, Friendship> repoPrietenie;
    private Long id;

    public UserService(Repo<Long, User> repo,Repo<Tuple<Long, Long>, Friendship> repoPrietenie) {
        this.repo = repo;
        this.repoPrietenie = repoPrietenie;
        id = maxContor();
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


    /***
     *Adds a new user with the given attributes:firstName,lastName
     * @param id:Long, must be greater than 0
     * @param firstName:String, must be not ""
     * @param lastName:String, must be not ""
     * @return task:Utilizator(firstName,lastName,null)
     *@throws ValidationException
     *              if the users exists already, or his attributes are invalid

     */
    public User addUser(Long id, String firstName, String lastName) {
        User utilizator = new User(firstName, lastName);
        utilizator.setId(id);
        User task = repo.save(utilizator);
        return task;
    }

    private Long maxContor() {
        Long m = 0l;
        if (repo.getAll().size() > 0) {
            m = repo.getAll().stream()
                    .map(x -> x.getId())
                    .max(Long::compare).get();
        }
        return m;

    }

    /***
     *Removes the user with given id and all his friendships
     * @param id:Long,must be not null
     * @return the removed user
     * @throws ValidationException
     *             if there is no user with the given id
     */
    public User removeUser(Long id) {
        User task = repo.delete(id);
        deleteUserFriendships(id);
        return task;
    }


    public void updateUserNotifications(Long id,String notif)
    {
        User user=repo.findOne(id);
        repo.delete(id);
        user.setNotifications(notif);
        repo.save(user);

    }

    public User getUser(Long id) {
        User user = repo.findOne(id);
        return user;
    }

    public ArrayList<User> getPossibleFriends(User user) {
        ArrayList<User> users = repo.getAll();
        return users;
    }

    public ArrayList<User> getAllUsers() {
        return repo.getAll();
    }

    public ArrayList<String> friendsof(Long id) {
        return (ArrayList<String>) repoPrietenie.getAll().stream()
                .filter(x -> {
                    return x.getId().getLeft() == id || x.getId().getRight() == id;
                })
                .map(x -> {
                    Long y;
                    if (x.getId().getRight() == id) y = x.getId().getLeft();
                    else y = x.getId().getRight();
                    return repo.findOne(y).toString() + " | " + x.getDate().toString();
                })
                .collect(Collectors.toList());

    }

    public ArrayList<User> friendsof_users(Long id) {
        return (ArrayList<User>) repoPrietenie.getAll().stream()
                .filter(x -> {
                    return x.getId().getLeft() == id || x.getId().getRight() == id;
                })
                .map(x -> {
                    Long y;
                    if (x.getId().getRight() == id) y = x.getId().getLeft();
                    else y = x.getId().getRight();
                    return repo.findOne(y);
                })
                .collect(Collectors.toList());


    }

    public ArrayList<String> friendsofByMonth(Long id, String month) {

        return (ArrayList<String>) repoPrietenie.getAll().stream()
                .filter(x -> {
                    return (x.getId().getLeft() == id || x.getId().getRight() == id) && x.getDate().getMonth().toString().equals(month.toUpperCase());
                })
                .map(x -> {
                    Long y;
                    if (x.getId().getRight() == id) y = x.getId().getLeft();
                    else y = x.getId().getRight();
                    return repo.findOne(y).toString() + " | " + x.getDate().toString();
                })
                .collect(Collectors.toList());

    }

    /***
     * All the users
     * @return all the users
     */
    public Iterable<User> getAll() {

        return repo.findAll();
    }

    /**
     * Returns all the friendships
     *
     * @return all the friendships
     */
    public List<Friendship> getPrietenii() {
        return StreamSupport.stream(repoPrietenie.findAll().spliterator(), false).collect(Collectors.toList());
    }

    /***
     * Deletes all the friendships of the user with the given id
     * @param user_id (Long): the user's id
     */
    void deleteUserFriendships(Long user_id) {
        ArrayList<Friendship> friendships = repoPrietenie.getAll();
        for (Friendship f : friendships) {
            if (f.getId().getLeft() == user_id || f.getId().getRight() == user_id)
                repoPrietenie.delete(f.getId());

        }
    }


    /**
     * Adds a new friendship between the user with the id1 and the user with the id2
     *
     * @param id1Str:String
     * @param id2Str:String
     * @throws ValidationException ,
     *                             if there are no users with those ids
     *                             if id1=id2
     *                             if there is already a friendship between those users
     */
    public void addFriendship(String id1Str, String id2Str) {
        Long id1 = FriendshipValidator.validLong(id1Str);
        Long id2 = FriendshipValidator.validLong(id2Str);
        if (id1 > id2) {
            Long aux = id1;
            id1 = id2;
            id2 = aux;
        }
        Friendship friendship = new Friendship(new Tuple<>(repo.findOne(id1).getId(), repo.findOne(id2).getId()));
        friendship.setDate(LocalDateTime.now());
        Friendship fr = repoPrietenie.save(friendship);
        if (fr == null) {
            notifyObservers(new UserChangeEvent(ChangeEventType.ADD, repo.findOne(id2)));
        }

    }

    public ArrayList<Activity> friendshipsActivityOfUser(Long id) {

        return (ArrayList<Activity>) repoPrietenie.getAll().stream()
                .filter(x -> {
                    return x.getId().getLeft() == id || x.getId().getRight() == id;
                })
                .map(x -> {
                    Long y;
                    if (x.getId().getRight() == id) y = x.getId().getLeft();
                    else y = x.getId().getRight();
                    return new Activity(repo.findOne(y).toString() + " became your friend.", x.getDate());
                })
                .collect(Collectors.toList());
    }

    /***
     * Removes the friendship between the users with the given ids
     * @param id1:Long
     * @param id2:Long
     * @throws ValidationException ,
     *          if there are no users with those ids
     *          if id1=id2
     *          if there is no friendship between those users
     */
    public void removeFriendship(Long id1, Long id2) {
        User delete_friend = repo.findOne(id2);
        if (id1 > id2) {
            Long aux = id1;
            id1 = id2;
            id2 = aux;
        }
        repoPrietenie.delete(new Tuple<>(repo.findOne(id1).getId(), repo.findOne(id2).getId()));

        notifyObservers(new UserChangeEvent(ChangeEventType.DELETE, null));


    }


    /***
     * Returns the number of communities of  users
     * @return number of communities:int
     */
    public int nrCommunity() {

        int nrVertices = repo.getAll().size();
        Graph graph = new Graph(nrVertices);

        repoPrietenie.findAll().forEach(x -> graph.addEdge(x.getId().getLeft(), x.getId().getRight()));
        int nr = graph.nrConnectedComponents();
        int visited = graph.nrVisitedVertices();
        int total = nrVertices - visited + nr;
        return total;

    }

    /***
     * Returns the largest community of users
     * @return the largest community:ArrayList
     */
    public ArrayList<Long> largestCommunity() {
        int nrVertices = repo.getAll().size();
        Graph graph = new Graph(nrVertices);
        repoPrietenie.findAll().forEach(x -> graph.addEdge(x.getId().getLeft(), x.getId().getRight()));
        return graph.getMaxComp();
    }

    /**
     * Returns the user with the given id
     * @param id: user id
     * @return user
     */
    public User find(Long id) {
        return repo.findOne(id);
    }


    /**
     * Log in the user with the given email and password
     * @param email: email address of the user
     * @param password: user password
     * @return
     * @throws ValidationException if the arguments are not correct
     */
    public User login(String email, String password) {
        String originalInput = password;
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());

        try {
            return repo.getAll().stream()
                    .filter(x -> x.getEmail().equals(email))
                    .filter((x -> x.getPassword().equals(encodedString)))
                    .collect(Collectors.toList())
                    .get(0);
        } catch (IndexOutOfBoundsException ex) {
            throw new ValidationException("Email address or password is invalid!");
        }

    }

    /**
     * Checks if an email is valid: is unique
     * @param email: given email
     */
    private void validUniqueEmail(String email)
    {
        if(repo.getAll().stream().filter(x->x.getEmail().equals(email)).count()>0) throw new ValidationException("This email address is not available! ");

    }


    /**
     * Creates and saves user with given arguments
     * @param firstName: user first name
     * @param lastName: user last name
     * @param email: user email
     * @param password: user password
     * @param gender: user gender
     * @param date: birth date
     */
    public void addUser(String firstName, String lastName, String email, String password, String gender, LocalDate date) {
        validUniqueEmail(email);
        String originalInput = password;
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());


        User utilizator = new User(firstName, lastName, email, gender, encodedString, date);
        id = id + 1;
        utilizator.setId(id);
        User task = repo.save(utilizator);
    }

    /**
     * Returns number of pages of users
     * @return number of current page
     */
    public int getUserPageCount() {
        return repo.getPageCount();
    }

    /**
     * Returns a list of the user from the given page , all users
     * @param pageNumber: page number
     * @return list of users
     */
    public Iterable<User> getPage(int pageNumber) {
        return repo.getPage(pageNumber);
    }

    public int getFriendsPageCount(Long id_user) {
        return repoPrietenie.getPageCountUser(id_user);
    }

    /**
     * Returns a list of the user from the given page , the friends of the user with id: id_user
     * @param pageNumber: page number
     * @param id_user: user id
     * @return list of users
     */
    public ArrayList<User> getFriendsPage(int pageNumber,Long id_user) {

        return (ArrayList<User>) StreamSupport.stream(repoPrietenie.getPageUser(pageNumber,id_user).spliterator(), false)
                .map(x -> {
            Long y=0l;
            if (x.getId().getRight() == id_user) y = x.getId().getLeft();
            else y = x.getId().getRight();
            return repo.findOne(y);

        }).collect(Collectors.toList());
    }


    public User findUser(Long id) {
        return repo.findOne(id);
    }
}