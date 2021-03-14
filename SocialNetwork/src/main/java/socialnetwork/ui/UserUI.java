package socialnetwork.ui;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserUI {
    private UserService userService;
    private MessageService messageService;
    private FriendshipRequestService requestService;
    private FriendshipRequestUI friendshipRequestUI;

    public UserUI(UserService utilizatorService, MessageService messageService,FriendshipRequestService requestService) {
        this.userService = utilizatorService;
        this.messageService=messageService;
        this.requestService=requestService;
        friendshipRequestUI=new FriendshipRequestUI(userService,requestService,messageService);
    }

    public void addUtilizator() {
        Scanner f = new Scanner(System.in);
        System.out.print("id=");
        long id=f.nextLong();

        System.out.print("first name=");
        String firstName=f.next();

        System.out.print("last name=");
        String lastName=f.next();

        try {
            userService.addUser(id,firstName, lastName);
           System.out.println("User: " + firstName +" " +lastName+ " has been added...");
        }catch (ValidationException exception){
                System.out.println(exception.getMessage());
        }


    }
    public void addFriend(){
        Scanner in= new Scanner(System.in);
        System.out.print("Give id1: ");
        String id1 =in.next();
        System.out.print("Give id2: ");
        String id2 =in.next();
        try{
            userService.addFriendship(id1,id2);
            System.out.println("Friendship added!...");
        }catch (ValidationException ex) {

            System.out.println(ex.getMessage());

        }

    }
    public void removeUser(){
        Scanner in= new Scanner(System.in);
        System.out.print("Give id user you want to delete: ");
        long id=in.nextLong();
        try{
            userService.removeUser(id);
            requestService.deleteUserFriendshipRequests(id);
            System.out.println("The user has been deleted!...");
        }catch(ValidationException exception) {
            System.out.println(exception.getMessage());
        }
    }
    public void removeFriend(){
        Scanner in= new Scanner(System.in);
        System.out.print("Give id1: ");
        String id1 =in.next();
        System.out.print("Give id2: ");
        String id2 =in.next();
        try{
            userService.removeFriendship(Long.parseLong(id1),Long.parseLong(id2));
            System.out.println("The friendship has been deleted!...");
        }catch (ValidationException ex){
            System.out.println(ex.getMessage());
        }
    }



    public void nrCommunity()
    {
        int nr = userService.nrCommunity();
        if (nr==0) System.out.println("There are no communities!");
        else System.out.println("There are " + nr + " communities.");
    }

    public void largestCommunity(){
        ArrayList<Long> array = userService.largestCommunity();
        if (array.size()==0) System.out.println("There are no communities!");
        else System.out.println("The most sociable community: "+array);
    }
    public void getAll(){
        userService.getAll().forEach(System.out::println);
    }

    private void friendsof() {
        Scanner in= new Scanner(System.in);
        System.out.print("Give id: ");
        Long id =in.nextLong();
        userService.friendsof(id).forEach(System.out::println);
    }
    private void friendsofMonth() {
        Scanner in= new Scanner(System.in);
        System.out.print("Give id: ");
        Long id =in.nextLong();
        System.out.print("Give month: ");
        String month =in.next();
        userService.friendsofByMonth(id,month).forEach(System.out::println);
    }


    public void menu()
    {
        Scanner in = new Scanner(System.in);
        while (true){
            System.out.println("\nChoose one of the commands:\n" +
                    "exit:Exit\n"+
                    "0:Print users\n" +
                    "1:Add user\n" +
                    "2:Delete user\n" +
                    "3:Add friendship\n" +
                    "4:Delete frienship\n" +
                    "5:Number of communities\n" +
                    "6:The most sociable community\n"+
                    "7:Friends of a user\n"+
                    "8:Friends of a user made in a specified month\n"+
                    "9:Print all messages\n"+
                    "10:Send message\n"+
                    "20:LOG IN\n");
            System.out.print("Choose a command: ");
            String c=in.next();
            switch (c){
                case "exit":
                    System.out.println("Bye!" );
                    return;
                case "0":
                    getAll();
                    break;
                case "1":
                    addUtilizator();
                    break;
                case "2":
                    removeUser();
                    break;
                case "3":
                    addFriend();
                    break;
                case "4":
                    removeFriend();
                    break;
                case "5":
                    nrCommunity();
                    break;
                case "6":
                    largestCommunity();
                    break;
                case "7":
                    friendsof();
                    break;
                case "8":
                    friendsofMonth();
                    break;
                case "9":
                    printMessages();
                    break;
                case "10":
                    sendMessage();
                    break;
                case "11":
                    receivedMessages();
                    break;
                case "12":
                    sentMessagesof();
                    break;
                case "13":
                    replyMessage();
                    break;
                case "14":
                    conversation();
                    break;
                case "20":
                    login();
                    break;
                default:
                    System.out.println("Invalid command!\n" );
                    break;
            }
        }
    }

    private void login() {
        Scanner in= new Scanner(System.in);
        System.out.print("Give user id to log in: ");
        Long id =in.nextLong();
        try {
            User user=userService.find(id);
            System.out.print("\n....... Welcome back, "+user.toString()+"! .......\n");
            friendshipRequestUI.setId_user(id);
            friendshipRequestUI.menu();
        }catch(ValidationException ex)
        {
            System.out.println("Id invalid!");
        }
    }

    private void conversation() {
        Scanner in= new Scanner(System.in);
        System.out.print("Give id1: ");
        Long id1 =in.nextLong();
        System.out.print("Give id2: ");
        Long id2 =in.nextLong();
        messageService.conversation(id1,id2).forEach(System.out::println);

    }

    private void replyMessage() {
        Scanner in= new Scanner(System.in);

        System.out.print("Give the message id : ");
        Long id =in.nextLong();
        System.out.print("Give message: ");
        in= new Scanner(System.in);
        String msg=in.nextLine();
        messageService.replyMessage(id,msg);
    }

    private void sentMessagesof() {
        Scanner in= new Scanner(System.in);
        System.out.print("Give id: ");
        Long id =in.nextLong();
        messageService.sentMessagesOf(id).forEach(System.out::println);
    }

    private void receivedMessages() {

        Scanner in= new Scanner(System.in);
        System.out.print("Give id: ");
        Long id =in.nextLong();
        messageService.receivedMessagesOf(id).forEach(System.out::println);
    }

    private void sendMessage() {
        Scanner in= new Scanner(System.in);

        System.out.println("Give sender id: ");
        Long sender_id =in.nextLong();
        List<Long> receive_list=new ArrayList<Long>();
        System.out.println("Give users id you want to send to: ");
        Long receiver_id=in.nextLong();
        while(receiver_id!=0l)
        {
            receive_list.add(receiver_id);
            System.out.println("Give users id you want to send to: ");
            receiver_id=in.nextLong();
        }
        System.out.print("Give message: ");
        in= new Scanner(System.in);
        String msg=in.nextLine();
        try{
            messageService.sendMessage(sender_id,receive_list,msg);
            System.out.println("Message sent.....");
        }catch (ValidationException exception)
        {
            System.out.println(exception.getMessage());
        }
    }

    private void printMessages() {

        messageService.getAll().forEach(System.out::println);
    }


}
