package socialnetwork.ui;

import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FriendshipRequestUI {
    private Long id_user;
    private UserService userService;
    private FriendshipRequestService friendshipRequestService;
    private MessageService messageService;

    public FriendshipRequestUI(UserService userService, FriendshipRequestService friendshipRequestService,MessageService messageService ){
        this.userService = userService;
        this.friendshipRequestService = friendshipRequestService;
        this.messageService=messageService;
    }

    public void setId_user(Long id_user) {
        this.id_user = id_user;
    }

    public void menu()
    {

        Scanner in = new Scanner(System.in);
        while (true){
            System.out.println("\nChoose one of the commands:\n" +
                    "exit:Exit\n"+
                    "0:Show  friendship requests\n" +
                    "1:Send a friendship request\n" +
                    "2:Respond a friendship request\n"+
                    "3:Show my friends\n"+
                    "4:Show my friends made on a specified month\n" +
                    "5:My messages\n"+
                    "6:Send a message\n"+
                    "7:Reply\n"+
                    "8:My conversation with\n");
            System.out.print("Choose a command: ");
            String c=in.next();
            switch (c){
                case "exit":
                    System.out.println("Bye!" );
                    return;
                case "0":
                    allFriendshipsRequest();
                    break;
                case "1":
                    sendFriendshipRequest();
                    break;
                case "2":
                    respondFriendshipRequest();
                    break;
                case "3":
                    myFriends();
                    break;
                case "4":
                    myFriendsMonth();
                    break;
                case "5":
                    myMessages();
                    break;
                case "6":
                    sendMessage();
                    break;
                case "7":
                    reply();
                    break;
                case "8":
                    conversation();
                    break;
                default:
                    System.out.println("Invalid command!\n" );
                    break;
            }
        }
    }

    private void conversation() {
        Scanner in= new Scanner(System.in);
        System.out.print("Give id of the conversation partner: ");
        Long id2 =in.nextLong();
        try {
            messageService.conversation(id_user, id2).forEach(System.out::println);
        }catch (ValidationException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private void reply() {
        System.out.println("\n____unreplied messages_____\n");
        messageService.unrepliedMsgOf(id_user).forEach(System.out::println);
        Scanner in= new Scanner(System.in);

        System.out.print("Give the message id : ");
        Long id_msg =in.nextLong();
        System.out.print("Give message: ");
        in= new Scanner(System.in);
        String msg=in.nextLine();
        try {

            messageService.replyMessageByUser(id_user, id_msg, msg);
        }catch(ValidationException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private void sendMessage() {
        Scanner in= new Scanner(System.in);
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
            messageService.sendMessage(id_user,receive_list,msg);
            System.out.println("Message sent.....");
        }catch (ValidationException exception)
        {
            System.out.println(exception.getMessage());
        }
    }

    private void myMessages() {
        System.out.println("\n_____Sent messages______\n");
        messageService.sentMessagesOf(id_user).forEach(System.out::println);
        System.out.println("\n______Received messages______\n");
        messageService.receivedMessagesOf(id_user).forEach(System.out::println);
    }

    private void myFriendsMonth() {
        Scanner in= new Scanner(System.in);
        System.out.print("Give month: ");
        String month =in.next();
        userService.friendsofByMonth(id_user,month).forEach(System.out::println);
    }

    private void myFriends() {
        userService.friendsof(id_user).forEach(System.out::println);;
    }

    private void respondFriendshipRequest() {
        Scanner in= new Scanner(System.in);

        System.out.print("Give the user id you want to respond the friendship request: ");
        Long id2 =in.nextLong();
        System.out.println("Type yes/no to accept/reject the friendship request");
        in= new Scanner(System.in);
        try {
            String choice=in.next();
            if(choice.equals("yes")) {
                friendshipRequestService.acceptRequest(id_user, id2);
                System.out.println("Friendship request accepted...");
            }
            else{
                friendshipRequestService.rejectRequest(id_user, id2);
                System.out.println("Friendship request rejected...");
            }
        }catch(ValidationException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private void sendFriendshipRequest() {
        Scanner in= new Scanner(System.in);

        System.out.print("Give the user id you want to send a friendship request: ");
        Long id2 =in.nextLong();
        try {
            friendshipRequestService.sendRequest(id_user, id2);
            System.out.println("Friendship request sent...");
        }catch(ValidationException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    private void allFriendshipsRequest() {
        friendshipRequestService.getNewRequests(id_user).forEach(System.out::println);
    }
}
