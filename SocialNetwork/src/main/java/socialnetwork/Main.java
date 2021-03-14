package socialnetwork;

public class Main {
    public static void main(String[] args) {
//        String fileName=ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
//        String friendshipsFile=ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friendships");
//
//
//        Repository<Long, User> userFileRepository = new UserFile(fileName
//                , new UserValidator());
//        Repository<Tuple<Long,Long>, Friendship> friendshipsFileRepository = new FriendshipFile(friendshipsFile
//                , new FriendshipValidator());
//        System.out.println("Reading data from database");
//        final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
//        final String username= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
//        final String password= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
//        Repository<Long, User> userDBRepository =
//                new UserDatabaseRepository(url,username, password,  new UserValidator());
//        Repository<Tuple<Long,Long>, Friendship> friendshipDBRepository =
//                new FriendshipDbRepository(url,username, password,  new FriendshipValidator());
//        Repository<Tuple<Long,Long>, FriendshipRequest> requestDBRepository =
//                new FriendshipRequestDbRepository(url,username, password,  new FriendshipRequestValidator());
//        Repository<Long, Message> messageDBRepository =
//                new MessageDbRepository(url,username, password,  new MessageValidator());
//        UserService service=new UserService(userDBRepository,friendshipDBRepository);
//
//        MessageService messageService=new MessageService(userDBRepository,messageDBRepository);
//        FriendshipRequestService friendshipRequestService=new FriendshipRequestService(userDBRepository,friendshipDBRepository,requestDBRepository );
//        UserUI ui=new UserUI(service,messageService,friendshipRequestService);


        MainFX.main(args);




    }
}


