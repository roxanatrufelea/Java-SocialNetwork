package socialnetwork.service;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FriendshipRequestService implements Observable<UserChangeEvent> {

    Repository<Long, User> repoUser;
    Repository<Tuple<Long, Long>, Friendship> repoFriendship;
    Repository<Tuple<Long,Long>, FriendshipRequest> repoRequest;

    public FriendshipRequestService(Repository<Long, User> userFileRepository, Repository<Tuple<Long, Long>, Friendship> friendshipsFileRepository, Repository<Tuple<Long, Long>, FriendshipRequest> friendshipsRequestFileRepository) {
        this.repoUser = userFileRepository;
        this.repoFriendship = friendshipsFileRepository;
        this.repoRequest = friendshipsRequestFileRepository;
    }

    private List<Observer<UserChangeEvent>> observers=new ArrayList<>();

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
        observers.stream().forEach(x->x.update(t));
    }



//    private List<Observer<UserChangeEvent>> observers=new ArrayList<>();
//
//    @Override
//    public void addObserver(Observer<UserChangeEvent> e) {
//        observers.add(e);
//
//    }
//
//    @Override
//    public void removeObserver(Observer<UserChangeEvent> e) {
//        //observers.remove(e);
//    }
//
//    @Override
//    public void notifyObservers(UserChangeEvent t) {
//        observers.stream().forEach(x->x.update(t));
//    }
//

    /**
     * Deletes all sended or received friendhip requests of a user
     * @param user_id: Long  :the id user to delete
     */
    public void deleteUserFriendshipRequests(Long user_id){
        ArrayList<FriendshipRequest> friendships = repoRequest.getAll();
        for (FriendshipRequest f:friendships) {
            if( f.getId().getLeft()==user_id||f.getId().getRight()==user_id)
                repoRequest.delete(f.getId());

        }
    }


    /**
     * Deletes the friendship between the users with the given ids
     * @param user_id id of the first user
     * @param id2: id of the second user
     */
    public void deleteFriendshipRequest(Long user_id,Long id2){
        ArrayList<FriendshipRequest> friendships = repoRequest.getAll();
        for (FriendshipRequest f:friendships) {
            if( f.getId().equals(new Tuple<>(user_id,id2))||  f.getId().equals(new Tuple<>(id2,user_id)))
                repoRequest.delete(f.getId());

        }
    }

    /**
     * Returns a list  with all friendship requests
     * @return an iterable with all friendship requests
     */
    public Iterable<FriendshipRequest> getAll(){

        return repoRequest.findAll();
    }


    /**
     * Returns a list with all the users
     * @return an iterable with all the users
     */
    public Iterable<User> getAllUsers(){

        return repoUser.findAll();
    }


    /**
     * Returns the user with the given id
     * @param id: id of the user we are looking for
     * @return user
     */
    public User getUser(Long id){
        User user=repoUser.findOne(id);
        return user;
    }



    /**
     * Returns a list with all friendship requests that are on pending
     * @param id:Long  :the user id to find the  friendship requests
     * @return list
     */
    public List<FriendshipRequest> getNewRequests(Long id){

        return repoRequest.getAll()
                .stream()
                .filter(x->x.getId().getRight()==id)
                .filter(x->x.getStatus().equals("pending"))
                .collect(Collectors.toList());
    }

    /**
     * Returns all the friendship requests sended by the user with the given id
     * @param id: id of the user
     * @return list of friendship requests
     */
    public List <User> getMySendedRequests(Long id){
        return repoRequest.getAll()
                .stream()
                .filter(x->x.getId().getLeft()==id)
                .filter(x->x.getStatus().equals("pending"))
                .map(x->repoUser.findOne(x.getId().getRight()))
                .collect(Collectors.toList());

    }

    /**
     * Returns a list of RequestDTO objects, all the friendship requests received by the user with the given id
     * @param id: given id of the user
     * @return list of requestDTO
     */
    public List<RequestDTO> getUsersRequest(Long id){
        return  getNewRequests(id)
                .stream()
                .map(x->{
                    User user=repoUser.findOne(x.getId().getLeft());
                    RequestDTO requestDTO=new RequestDTO(user.getFirstName(), user.getLastName(), x.getDate());
                    requestDTO.setId(user.getId());
                    return requestDTO;
                })
                .collect(Collectors.toList());
    }

    /**
     * Creates a FriendshipRequest object and adds it in the file
     * @param id_user:Long    the sender id
     * @param id2:Long        the receiver id
     * @throws ValidationException if there is already a friendship request with that id
     */
    public void sendRequest(Long id_user, Long id2) {
       if (repoRequest.exists(new Tuple<Long, Long>(id_user,id2)))
        {
            if (repoRequest.findOne(new Tuple<Long, Long>(id_user, id2)).getStatus().equals("rejected"))
                repoRequest.delete(new Tuple<Long, Long>(id_user, id2));
        }
        if(!repoRequest.exists(new Tuple<Long, Long>(id_user,id2)) && !repoFriendship.exists(new Tuple<Long, Long>(id_user,id2)) && !repoRequest.exists(new Tuple<Long, Long>(id2,id_user)) && !repoFriendship.exists(new Tuple<Long, Long>(id2,id_user)))
        {
            FriendshipRequest friendshipRequest=new FriendshipRequest(new Tuple<Long, Long>(repoUser.findOne(id_user).getId(),repoUser.findOne(id2).getId()),"pending");
            friendshipRequest.setDate(LocalDateTime.now());
            repoRequest.save(friendshipRequest);
            notifyObservers(new UserChangeEvent(ChangeEventType.ADD, repoUser.findOne(id2)));
        }
        else throw new ValidationException("You have already sent a friendship request");
    }

    /**
     * Updates a FriendshipRequest from the status pending to accepted and saves the Friendship object created
     * @param id_user:Long : the id of the request sender
     * @param id2:Long   :the id of the request receiver
     * @throws ValidationException if there is no FriendshipRequest object with that id
     */
    public void acceptRequest(Long id_user, Long id2) {
        if(repoRequest.findOne(new Tuple<Long, Long>(id2,id_user)).getStatus().equals("pending"))
        {
            repoRequest.delete(new Tuple<Long, Long>(id2,id_user));
            FriendshipRequest friendshipRequest=new FriendshipRequest(new Tuple<Long, Long>(repoUser.findOne(id2).getId(),repoUser.findOne(id_user).getId()),"accepted");
            friendshipRequest.setDate(LocalDateTime.now());
            repoRequest.save(friendshipRequest);
            if(id_user>id2) {Long aux=id_user;id_user=id2;id2=aux;}
            repoFriendship.save(new Friendship(new Tuple<>(repoUser.findOne(id_user).getId(),repoUser.findOne(id2).getId()), LocalDateTime.now()));
            notifyObservers(new UserChangeEvent(ChangeEventType.ADD, repoUser.findOne(id2)));

        }
        else throw new ValidationException("You can't accept this friendship");
    }


    /**
     * Updates a FriendshipRequest from the status pending to rejected
     * @param id_user:Long : the id of the request sender
     * @param id2:Long   :the id of the request receiver
     * @throws ValidationException if there is no FriendshipRequest object with that id
     */
    public void rejectRequest(Long id_user, Long id2) {
        if(repoRequest.findOne(new Tuple<Long, Long>(id2,id_user)).getStatus().equals("pending"))
        {
            repoRequest.delete(new Tuple<Long, Long>(id2,id_user));
            notifyObservers(new UserChangeEvent(ChangeEventType.DELETE, repoUser.findOne(id2)));


        }
        else throw new ValidationException("You can't reject this friendship");
    }

    /**
     * Returns a list with all the users that are friends with the user with the given id
     * @param id: id of the given user
     * @return list of users
     */
    public ArrayList<User> friendsof(Long id)
    {
        return (ArrayList<User>) repoFriendship.getAll().stream()
                .filter(x->{
                    return x.getId().getLeft()==id || x.getId().getRight()==id;
                })
                .map(x->{
                    Long y;
                    if(x.getId().getRight()==id) y=x.getId().getLeft();
                    else y=x.getId().getRight();
                    return repoUser.findOne(y);})
                .collect(Collectors.toList());

    }

    /**
     * Deletes the friendship between the users with the given ids
     * @param id1Str: id of user 1
     * @param id2Str: id of user 2
     */
    public void removeFriendship(String id1Str, String id2Str){
        Long id1= FriendshipValidator.validLong(id1Str);
        Long id2= FriendshipValidator.validLong(id2Str);
        if(id1>id2) {Long aux=id1;id1=id2;id2=aux;}
        repoFriendship.delete(new Tuple<>(repoUser.findOne(id1).getId(),repoUser.findOne(id2).getId()));
    }

    /**
     * Deletes the friendship request between the users with the given ids
     * @param id: id of user 1
     * @param id1: id of user 2
     */
    public void deleteRequest(Long id, Long id1) {
        repoRequest.delete(new Tuple<>(id,id1));
        notifyObservers(new UserChangeEvent(ChangeEventType.DELETE, repoUser.findOne(id1)));

    }
}
