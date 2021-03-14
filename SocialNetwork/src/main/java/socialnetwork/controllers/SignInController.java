package socialnetwork.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.EventsService;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.io.IOException;
import java.time.LocalDate;

public class SignInController {
    @FXML
    private ComboBox cboxDay;
    @FXML
    private ComboBox cboxMonth;
    @FXML
    private ComboBox cboxYear;
    @FXML
    private ComboBox cboxGender;
    @FXML
    private PasswordField textFieldCheckPassword;
    @FXML
    private TextField textFieldLastName;
    @FXML
    private TextField textFieldFirstName;
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


    public void setService(UserService userService, FriendshipRequestService requestService, MessageService messageService,EventsService eventService, Stage primaryStage, User user)
    {
        this.userService=userService;
        this.requestService = requestService;
        this.messageService=messageService;
        this.eventService=eventService;
        this.primaryStage=primaryStage;
    }

    @FXML
    private void initialize() {
        cboxGender.getItems().addAll("Male","Female","Neutral");
        cboxMonth.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12);

        for(Integer i=1;i<=31;i++)
            cboxDay.getItems().add(i);
        for(Integer i=1960;i<=2010;i++)
            cboxYear.getItems().add(i);


    }

    public void handleSignUp(ActionEvent actionEvent) {

        try {
            String firstName = textFieldFirstName.getText();
            String lastName = textFieldLastName.getText();
            String password = textFieldPassword.getText();
            String checkPassword = textFieldCheckPassword.getText();
            String email = textFieldEmail.getText();
            String gender = (String) cboxGender.getValue();
            Integer day = (Integer) cboxDay.getValue();
            Integer month = (Integer) cboxMonth.getValue();
            Integer year = (Integer) cboxYear.getValue();
            LocalDate date = LocalDate.of(year, month, day);
            if (password.equals(checkPassword)) {
                try {
                    userService.addUser(firstName, lastName, email, password, gender, date);
                    LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Sign Up OK", "You have just created a new account!");
                    handleLogIn();
                } catch (ValidationException | IOException ex) {
                    textFieldPassword.clear();
                    textFieldCheckPassword.clear();
                    textFieldEmail.clear();
                    LogInAlert.showErrorMessage(null, ex.getMessage());
                }

            } else {
                textFieldCheckPassword.clear();
                textFieldPassword.clear();
                LogInAlert.showErrorMessage(null, "Check your password!");
            }
        } catch (NullPointerException ex) {

            LogInAlert.showErrorMessage(null,"Every text field must be completed!");

        }
    }
        public void handleLogIn() throws IOException {
            primaryStage.setWidth(565);
            primaryStage.setHeight(460);
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(getClass().getResource("/views/usersView.fxml"));

            AnchorPane layout = loader.load();
            primaryStage.setScene(new Scene(layout));

            MainController controller = loader.getController();
            controller.setService(userService,requestService,messageService,eventService,primaryStage);
        }

    public void handleCancel(MouseEvent actionEvent) throws IOException {

        handleLogIn();
    }
}
