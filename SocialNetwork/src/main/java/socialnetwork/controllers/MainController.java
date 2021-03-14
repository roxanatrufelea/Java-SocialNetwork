package socialnetwork.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.EventsService;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.io.IOException;

public class MainController {
    @FXML
    public AnchorPane anchorPaneRight;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldPassword;

    FriendshipRequestService requestService;
    UserService userService;
    MessageService messageService;
    EventsService eventService;
    Stage primaryStage;
    User user;


    public void setService(UserService userService,FriendshipRequestService requestService,MessageService messageService,EventsService eventService,Stage primaryStage) {
        this.userService=userService;
        this.requestService = requestService;
        this.messageService=messageService;
        this.eventService=eventService;
        this.primaryStage=primaryStage;
    }

    @FXML
    private void initialize() {

    }

    @FXML
    private void handleLogIn() {

        String email = textFieldEmail.getText();
        String password =textFieldPassword.getText();
        try {

            user = userService.login(email,password);
            System.out.println(user);
            LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Logged In", "Welcome back, " + user.toString() + " !");

            showMyPage();

        } catch (ValidationException ex) {
            LogInAlert.showErrorMessage(null, ex.getMessage());
        }catch(IOException e){
        e.printStackTrace();
    }


    }

    private void showMyPage() throws IOException {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/adminPageView.fxml"));
            AnchorPane layout = loader.load();
            System.out.println("---------");
            // Create the dialog Stage.
            Stage primaryStage = new Stage();
            String name=user.toString();
            primaryStage.setTitle(name);


            primaryStage.setScene(new Scene(layout));
            AdminController adminController = loader.getController();
            adminController.setService(userService,requestService,messageService,eventService, primaryStage, user,"About");
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void handleSignUp(ActionEvent actionEvent) {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/SignUpView.fxml"));
            AnchorPane layout = loader.load();

            primaryStage.setTitle("SignUp");


            primaryStage.setScene(new Scene(layout));
            SignInController signInController = loader.getController();
            signInController.setService(userService,requestService,messageService,eventService, primaryStage, user);
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }



    }
}
