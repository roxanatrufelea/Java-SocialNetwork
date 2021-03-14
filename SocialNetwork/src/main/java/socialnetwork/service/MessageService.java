package socialnetwork.service;


import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repo;
import socialnetwork.utils.events.ChangeEventType;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageService implements Observable<UserChangeEvent> {
    private Long contor;
    private Long id_group;
    private Repo<Long, User> repoUser;
    private Repo<Long,Message> repoMsg;
    private Repo<Long, Group> repoGroup;


    public MessageService(Repo<Long, User> repoUser, Repo<Long, Message> repoMsg,Repo<Long, Group> repoGroup) {
        this.repoUser = repoUser;
        this.repoMsg = repoMsg;
        this.repoGroup=repoGroup;
        contor=maxContorMsg();
        id_group=maxContorGroup();
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



    /**
     * returns the maximum id of the messages
     * @return m:Long the maximum id
     */
    private Long maxContorMsg()
    {
        Long m=0l;
        if(repoMsg.getAll().size()>0) {
            m = repoMsg.getAll().stream()
                    .map(x -> x.getId())
                    .max(Long::compare).get();
        }
        System.out.println(m);
        return m;

    }
    private Long maxContorGroup()
    {
        Long m=0l;
        if(repoGroup.getAll().size()>0) {
            m = repoGroup.getAll().stream()
                    .map(x -> x.getId())
                    .max(Long::compare).get();
        }
        System.out.println(m);
        return m;

    }

    /**
     * Returns an Iterable object which contains all messages
     * @return all messages
     */
    public Iterable<Message> getAll() {
        return repoMsg.findAll();
    }

    /**
     * Creates and saves a new message with the given attributes
     * @param sender_id: the sender id
     * @param receive_list: the receiver id
     * @param msg: the text messages
     * @throws ValidationException if the sender or user id doesn't exist
     */
    public Long sendMessage(Long sender_id, List<Long> receive_list, String msg) throws ValidationException {
        receive_list.forEach(x->repoUser.findOne(x));
        contor++;
        Message message=new Message(contor,sender_id,receive_list,msg, LocalDateTime.now());
        if(repoUser.exists(sender_id)==false) throw new ValidationException("There is no user with this sender id");
        if(receive_list.stream()
                .anyMatch(x->repoUser.exists(x)==false)) throw new ValidationException("There is no user with this id");
        repoMsg.save(message);
        System.out.println(message);
        return message.getId();
    }

    /**
     * Sends a message in a group
     * @param group_id: id of the current group
     * @param sender_id: id of the user that sends the message
     * @param group_users: participants of the group
     * @param msg: message text
     */
    public void sendMessageGroup(Long group_id,Long sender_id, List<Long> group_users, String msg)
    {
        group_users.remove(sender_id);
        Long newMsg_id=sendMessage(sender_id,group_users,msg);
        Group group=repoGroup.findOne(group_id);
        ArrayList<Long> group_messages=group.getMessages();
        group_messages.add(newMsg_id);
        group.setMessages(group_messages);
        repoGroup.delete(group_id);
        repoGroup.save(group);
        System.out.println(group);

        notifyObservers(new UserChangeEvent(ChangeEventType.ADD, null));


    }

    /**
     * Creates a Group object with the given name and users
     * @param name: name of the group
     * @param users: participants of the group
     */
    public void createGroup(String name,ArrayList<Long> users)
    {
        System.out.println(id_group);
        id_group++;
        repoGroup.save(new Group(id_group,name,users));
        notifyObservers(new UserChangeEvent(ChangeEventType.ADD, null));
    }

    /**
     * Returns a list with all the groups
     * @return list of groups
     */
    public List<Group> allGroups(){

        return repoGroup.getAll();

    }

    /**
     * Returns a list with all the messages of the given group
     * @param group: given group
     * @return list of messages
     */
    public ArrayList<Message> getAllMessagesGroup(Group group){
        if(group.getMessages().size()==0) {
            System.out.println("getAllMessagesGroup");
            return new ArrayList<Message>();
        }
        return (ArrayList<Message>) repoGroup.findOne(group.getId()).getMessages()
                .stream()
                .map(x->repoMsg.findOne(x))
                .sorted((x,y)->x.getDate().compareTo(y.getDate()))
                .collect(Collectors.toList());


    }

    /**
     * Returns all MessagesDTOGroup of the given group
     * @param group: current group
     * @return  a list
     */
    public ArrayList<MessageDTO> getAllMessagesDTOGroup(Group group){
        return (ArrayList<MessageDTO>) getAllMessagesGroup(group)
                .stream()
                .map(x->{
                    MessageDTO msgDTO=new MessageDTO(x.getId(),repoUser.findOne(x.getFrom()), x.getMessage(), x.getDate());
                    msgDTO.setReply(x.getReply());
                    return msgDTO;
                })
                .collect(Collectors.toList());

    }

    /**
     * returns a list with all the participants of the given group
     * @param group: given group
     * @return list of users
     */
    public ArrayList<User> getAllUsersGroup(Group group){
        return (ArrayList<User>) repoGroup.findOne(group.getId()).getUsers()
                .stream()
                .map(x->repoUser.findOne(x))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list with all the groupDTOs
     * @return list
     */
    public ArrayList<GroupDTO> getAllGroupDTO() {
        return (ArrayList<GroupDTO>) repoGroup.getAll()
                .stream()
                .map(x -> new GroupDTO(x.getId(), x.getName(), getAllUsersGroup(x), getAllMessagesGroup(x)))
                .collect(Collectors.toList());
    }

    /**
     * Returns the last message of the given group
     * @param group: current group
     * @return the last messsage of the group
     */
    public Message getLastMessageOFGroup(Group group){

        if (getAllMessagesDTOGroup(group).size()==0) return null;
        return getAllMessagesGroup(group).get(getAllMessagesGroup(group).size()-1);
    }

    /**
     * Returns a list with all the GroupLastDTO  of the page with the number: pageNumber of the user with the given id
     * @param pageNumber
     * @param user_id
     * @return
     */
    public ArrayList<GroupLastDTO> getAllGroupLastDTO(int pageNumber,Long user_id){

        return (ArrayList<GroupLastDTO>) StreamSupport.stream(getPageGroup(pageNumber,user_id).spliterator(),false)
                .map(x->{
                    //getAllMessagesGroup(x).forEach(System.out::println);
                        Message lastMessage=getLastMessageOFGroup(x);
                        String name=x.getName();
                        if(x.getUsers().size()==2)for(Long id:x.getUsers())if(id!=user_id)name=repoUser.findOne(id).toString();
                        if (lastMessage==null) return new GroupLastDTO(x.getId(),name,null, null, null);
                        return new GroupLastDTO(x.getId(),name,repoUser.findOne(lastMessage.getFrom()), lastMessage.getMessage(), lastMessage.getDate());
                }).collect(Collectors.toList());

    }

    /**
     * Returns a list with all the GroupLastDTO of the user with the given id
     * @param user_id: user id
     * @return arraylist grouplastdtos
     */
    public ArrayList<GroupLastDTO> getAllGroupLastDTOO(Long user_id){

        ArrayList<GroupLastDTO> all= (ArrayList<GroupLastDTO>) repoGroup.getAll().stream()
                .filter(x->x.getUsers().contains(user_id))
                .map(x->{
                    //getAllMessagesGroup(x).forEach(System.out::println);
                    Message lastMessage=getLastMessageOFGroup(x);
                    String name=x.getName();
                    if(x.getUsers().size()==2)for(Long id:x.getUsers())if(id!=user_id)name=repoUser.findOne(id).toString();
                    if (lastMessage==null) return new GroupLastDTO(x.getId(),name,null, null, null);
                    return new GroupLastDTO(x.getId(),name,repoUser.findOne(lastMessage.getFrom()), lastMessage.getMessage(), lastMessage.getDate());
                })
                .collect(Collectors.toList());
       return all;
    }

    /**
     * returns a list with all received messages of the given user
     * @param id :user id
     * @return list of messages
     */


    public List<Message> receivedMessagesOf(Long id)
    {
        return repoMsg.getAll().stream()
                .filter(x->x.getTo().contains(id)).collect(Collectors.toList());
    }

    /**
     * returns a list with all sent messages of the given user
     * @param id :user id
     * @return list of messages
     */
    public List<Message> sentMessagesOf(Long id)
    {
        return repoMsg.getAll().stream()
                .filter(x->x.getFrom()==id).collect(Collectors.toList());
    }

    /**
     * Returns a list with all messages between the given users
     * @param id1: user id
     * @param id2 user id
     * @return
     */
    public List<Message> conversation(Long id1,Long id2){

        return repoMsg.getAll().stream()
                .filter(x->x.getTo().size()==1)
                .filter(x->{
                    return x.getFrom()==repoUser.findOne(id1).getId()|| x.getFrom()==repoUser.findOne(id2).getId();
                })
                .filter((x->{
                    return x.getTo().get(0)==id1||x.getTo().get(0)==repoUser.findOne(id2).getId();
                }))
                .sorted((x,y)->x.getDate().compareTo(y.getDate()))
                .collect(Collectors.toList());
    }

    /**
     * Turns a list of MessageDTO in a list of Activity
     * @param messageDTOS
     * @return list of activities
     */
    public ArrayList<Activity> receivedMessagesDTO(List<MessageDTO> messageDTOS)
    {
        List<MessageDTO> msgs= messageDTOS;
        return (ArrayList<Activity>) msgs.stream()
                .map(x->new Activity(x.getFrom().toString()+": "+x.getMessage(),x.getDate()))
                .collect(Collectors.toList());
    }


    /**
     * Replies a message given by id
     * @param id : message id
     * @param msg: text message
     */
    public Message replyMessage(Long id,String msg) {
        Message message=repoMsg.findOne(id);
        repoMsg.delete(id);
        message.setReply(msg);
        repoMsg.save(message);
        List<Long> list=new ArrayList<>();
        list.add(message.getFrom());
        contor++;
        Message new_msg=new Message(contor,message.getTo().get(0),list,msg,LocalDateTime.now());
        repoMsg.save(new_msg);

        return new_msg;
    }

    /**
     * Reply the message with the id message_id, from the group group_id with text : msg
     * @param group_id: group id
     * @param message_id: message id
     * @param msg: reply text
     */
    public void replyGroupMessage(Long group_id,Long message_id,String msg)
    {

        Group group=repoGroup.findOne(group_id);
        ArrayList<Long> messages=group.getMessages();
        messages.add(replyMessage(message_id,msg).getId());
        group.setMessages(messages);
        repoGroup.delete(group_id);
        repoGroup.save(group);
        notifyObservers(new UserChangeEvent(ChangeEventType.ADD, null));

    }

    /**
     * Replies a message received by a user, given by id
     * @param user_id : user id
     * @param id : message id
     * @param msg: text message
     */
    public void replyMessageByUser(Long user_id,Long id,String msg) {
        if(!messagesOf(user_id).contains(repoMsg.findOne(id)))
        {
            System.out.println("nu a primit mesajul asta....");
            throw new ValidationException("You can't reply that message!");
        }
        Message message=repoMsg.findOne(id);
        repoMsg.delete(id);
        message.setReply(msg);
        repoMsg.save(message);
        List<Long> list=new ArrayList<>();
        list.add(message.getFrom());
        message.getTo().forEach(x-> list.add(x));
        list.remove(user_id);
        contor++;
        repoMsg.save(new Message(contor,user_id,list,msg,LocalDateTime.now()));
        notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE, null));

    }
    /**
     * returns a list with all unreplied messages of the given user
     * @param id :user id
     * @return list of messages
     */
    public List<Message> unrepliedMsgOf(Long id)
    {
        return repoMsg.getAll().stream()
                .filter(x->x.getTo().get(0)==id)
                .filter(x->{return x.getReply()==null||x.getReply().equals("null");})
                .collect(Collectors.toList());
    }
    /**
     * returns a list with all messages of the given user
     * @param id :user id
     * @return list of messages
     */
    public List<Message> messagesOf(Long id)
    {
        return repoMsg.getAll().stream()
                .filter(x->x.getTo().contains(id))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list with the activity of a chat of the user with the id : id_uset
     * @param id_user: user id
     * @return list of activities
     */
    public ArrayList<Activity> receivedMessagesToActivityUser(Long id_user)
    {

        return (ArrayList<Activity>) messagesOf(id_user)
                .stream()
                .map(x->new Activity(repoUser.findOne(x.getFrom()).toString()+": "+x.getMessage(),x.getDate()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the group with the given id
     * @param id: group id
     * @return group
     */

    public Group getGroup(Long id) {
        return repoGroup.findOne(id);
    }

    /**
     * Returns the string that merges the names of participants of a group
     * @param group: the given group
     * @return list
     */
    public String getParticipantsGroup(Group group){

        return group.getUsers().stream()
                .map(x->repoUser.findOne(x).toString())
                .reduce("",(participants,x)->participants+x+";");

    }

    /**
     * Returns the number of pages of messages
     * @return number of pages
     */
    public int getMessagePageCount() {
        return repoMsg.getPageCount();
    }

    /**
     * Returns the number of pages of messages of the user with the given id
     * @param id: user id
     * @return number of pages
     */
    public int getMessagePageCountUser(Long id) {
        return repoMsg.getPageCountUser(id);
    }

    /**
     * Returns the messages of the page with the given number of the user with the given id
     * @param pageNumber: page number
     * @param id: user id
     * @return messages
     */
    public Iterable<Message> getPageMessage(int pageNumber,Long id) {
        return repoMsg.getPageUser(pageNumber,id);
    }


    public int getGroupPageCount() {
        return repoGroup.getPageCount();
    }

    public int getGroupPageCountUser(Long id) {
        return repoGroup.getPageCountUser(id);
    }

    public Iterable<Group> getPageGroup(int pageNumber,Long id) {
        return repoGroup.getPageUser(pageNumber,id);
    }
}
