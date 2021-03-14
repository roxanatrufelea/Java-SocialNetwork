package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controllers.MainController;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repo;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.service.EventsService;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.io.IOException;


public class MainFX extends Application{
    UserService userService;
    FriendshipRequestService requestService;
    MessageService messageService;
    EventsService eventService;


    @Override
    public void start(Stage primaryStage) throws IOException {

        final String url = ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.url");
        final String username= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.username");
        final String password= ApplicationContext.getPROPERTIES().getProperty("database.socialnetwork.password");
        Repo<Long, User> userDBRepository =
                new UserDatabaseRepository(url,username, password,  new UserValidator());
        Repo<Long,Event> eventDBRepository=new EventDbRepository(url,username,password,new EventValidator() );
        Repo<Tuple<Long,Long>, Friendship> friendshipDBRepository =
                new FriendshipDbRepository(url,username, password,  new FriendshipValidator());
        Repository<Tuple<Long,Long>, FriendshipRequest> requestDBRepository =
                new FriendshipRequestDbRepository(url,username, password,  new FriendshipRequestValidator());
        Repo<Long, Message> messageDBRepository =
                new MessageDbRepository(url,username, password,  new MessageValidator());
        Repo<Long, Group> groupDBRepository =
                new GroupDbRepository(url,username, password,  new GroupValidator());
        userService=new UserService(userDBRepository,friendshipDBRepository);
        eventService=new EventsService(userDBRepository,eventDBRepository);
        messageService=new MessageService(userDBRepository,messageDBRepository,groupDBRepository);
        requestService=new FriendshipRequestService(userDBRepository,friendshipDBRepository,requestDBRepository );

        initView(primaryStage);
        primaryStage.setWidth(565);
        primaryStage.setHeight(460);
        primaryStage.show();


    }

    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader loader = new FXMLLoader();

        loader.setLocation(getClass().getResource("/views/usersView.fxml"));

        AnchorPane layout = loader.load();
        primaryStage.setScene(new Scene(layout));

        MainController controller = loader.getController();
        controller.setService(userService,requestService,messageService,eventService,primaryStage);

    }

    public static void main(String[] args) {
        System.out.println("ok");
        launch(args);
    }
}
