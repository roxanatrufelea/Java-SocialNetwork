package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.EventsService;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SendRequestController implements Observer<UserChangeEvent> {

    public TextField textFieldName;
    public Button buttonDeleteRequest;
    public Button buttonAddFriend;
    public Button btnPrevious;
    public Button btnNext;
    public Label labelPageNumber;
    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, String> tableColumnFirstName;
    @FXML
    TableColumn<User, String> tableColumnLastName;

    private FriendshipRequestService requestService;
    private UserService userService;
    private MessageService messageService;
    EventsService eventService;
    private Stage stage;
    private User user;
    private final ObservableList<User> model = FXCollections.observableArrayList();
    int pageNumberFriends=0;

    public void setService(UserService userService, FriendshipRequestService requestService,MessageService messageService,EventsService eventService, Stage stage, User user) {
        this.requestService=requestService;
        this.userService=userService;
        this.messageService=messageService;
        this.eventService=eventService;
        this.user=user;
        this.stage=stage;
        userService.addObserver(this);
        requestService.addObserver(this);
        initModel();
    }

    private void initModel() {
//        ArrayList<User> users = userService.getPossibleFriends(user);
//        model.setAll(users);
        init();
        List<User> users= StreamSupport.stream(userService.getPage(pageNumberFriends).spliterator(),false).collect(Collectors.toList());
        users.removeIf(x->x.getId().equals(user.getId()));
        model.setAll(users);

    }

    private void initModelMyRequests() {
//        ArrayList<User> users = userService.getPossibleFriends(user);
//        model.setAll(users);
        buttonAddFriend.setDisable(true);
        buttonAddFriend.setVisible(false);
        buttonDeleteRequest.setVisible(true);
        buttonDeleteRequest.setDisable(true);
        init();
        List<User> users = requestService.getMySendedRequests(user.getId());
        model.setAll(users);
    }

    private void init() {
        if(pageNumberFriends>=userService.getUserPageCount())pageNumberFriends=userService.getUserPageCount()-1;
        if(pageNumberFriends<0) pageNumberFriends=0;
        btnNext.setDisable(pageNumberFriends==userService.getUserPageCount()-1);
        btnPrevious.setDisable(pageNumberFriends==0);
        labelPageNumber.setText("Page"+(pageNumberFriends+1)+"/"+userService.getUserPageCount());
    }


    @FXML
    private void initialize(){

        tableView.setItems(model);
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        buttonAddFriend.setVisible(true);
        buttonAddFriend.setDisable(true);
        buttonDeleteRequest.setVisible(false);
        tableView.setOnMouseClicked(e->handleActiveButtons());

    }

    private void handleActiveButtons() {
        if(buttonDeleteRequest.isVisible()) buttonDeleteRequest.setDisable(false);
        if(buttonAddFriend.isVisible()) buttonAddFriend.setDisable(false);
    }

    public void handleClose() {

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/adminPageView.fxml"));
            AnchorPane layout = loader.load();
            System.out.println("---------");
            // Create the dialog Stage.

            String name=user.toString();
            stage.setTitle(name);


            stage.setScene(new Scene(layout));
            AdminController adminController = loader.getController();
            adminController.setService(userService,requestService,messageService,eventService, stage, user,"Friends");
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void handleAddFriend()  {

        User u= tableView.getSelectionModel().getSelectedItem();
        if(u == null) {
            LogInAlert.showErrorMessage(null, "You haven't selected anybody!");
        }
        else {
            try {
                requestService.sendRequest(user.getId(),u.getId());
                LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Send request OK", "The friendship request was sent successfully!");
            }catch(ValidationException ex) {
                LogInAlert.showErrorMessage(null, ex.getMessage());
            }
        }


    }
    public void textFieldFilter() {
        model.setAll(userService.getPossibleFriends(user)
                .stream()
                .filter(x -> {
                    // first name
                    String text = textFieldName.getText();
                    return( x.getFirstName().toLowerCase().startsWith(text.toLowerCase())||x.getLastName().toLowerCase().startsWith(text.toLowerCase()));

                })

                .collect(Collectors.toList())
        );
    }

    @Override
    public void update(UserChangeEvent userChangeEvent) {
        initModel();
        initModelMyRequests();
    }

    public void handleFindNewFriends()  {

        btnNext.setVisible(true);
        btnPrevious.setVisible(true);
        labelPageNumber.setVisible(true);
        initModel();
        buttonDeleteRequest.setVisible(false);
        buttonAddFriend.setVisible(true);
        buttonAddFriend.setDisable(true);
    }

    public void handleDeleteRequests()  {

        User u= tableView.getSelectionModel().getSelectedItem();
        if(u == null) {
            LogInAlert.showErrorMessage(null, "You haven't selected anybody!");
        }
        else {
            try {
                requestService.deleteRequest(user.getId(),u.getId());
                LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Delete request OK", "The friendship request was deleted successfully!");
            }catch(ValidationException ex) {
                LogInAlert.showErrorMessage(null, ex.getMessage());
                handleRequests();
            }

        }
    }

    public void handleMyRequests()  {

       handleRequests();
    }
    public void handleRequests()  {

        btnNext.setVisible(false);
        btnPrevious.setVisible(false);
        labelPageNumber.setVisible(false);
        List<User> users = requestService.getMySendedRequests(user.getId());
        model.setAll(users);
        buttonAddFriend.setVisible(false);
        buttonDeleteRequest.setVisible(true);
        buttonDeleteRequest.setDisable(true);
    }

    public void handlePrevious() {
        pageNumberFriends--;
        initModel();
    }

    public void handleNext() {
        pageNumberFriends++;
        initModel();
    }
}
