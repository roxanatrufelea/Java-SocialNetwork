package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SendMessageController implements Observer<UserChangeEvent> {
    public Button btnPrevious;
    public Button btnNext;
    public Label labelPageNumber;
    public TextField textFieldNameGroup;
    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, String> tableColumnFirstName;
    @FXML
    TableColumn<User, String> tableColumnLastName;
    @FXML
    TextField textFieldName;
    int pageNumber=0;


    private UserService userService;
    private MessageService messageService;
    EventsService eventService;
    private Stage primaryStage;
    private FriendshipRequestService requestService;
    private User user;
    private ObservableList<User> model = FXCollections.observableArrayList();

    public void setService(UserService userService,FriendshipRequestService requestService, MessageService messageService, EventsService eventService,Stage stage, User user) {
        this.userService = userService;
        this.requestService=requestService;
        this.messageService = messageService;
        this.eventService=eventService;
        primaryStage = stage;
        this.user = user;
        tableView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        messageService.addObserver(this);

        initModel();
    }

    private void initModel() {
        if(pageNumber>=userService.getUserPageCount())pageNumber=userService.getUserPageCount()-1;
        if(pageNumber<0) pageNumber=0;
        btnNext.setDisable(pageNumber==userService.getUserPageCount()-1);
        btnPrevious.setDisable(pageNumber==0);
        labelPageNumber.setText("Page"+(pageNumber+1)+"/"+userService.getUserPageCount());

        List<User> users= StreamSupport.stream(userService.getPage(pageNumber).spliterator(),false).collect(Collectors.toList());
        model.setAll(users);


    }

    @FXML
    private void initialize() {
        tableView.setItems(model);
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

    }
    public void textFieldFilter(KeyEvent keyEvent) {
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


    public void handleNewGroup(ActionEvent actionEvent) {
        String nameGroup = textFieldNameGroup.getText();
        ObservableList<User> users = tableView.getSelectionModel().getSelectedItems();

        if (users == null) {
            LogInAlert.showErrorMessage(null, "You haven't selected any user!");
        }
        else if (users.size()>=2 &&nameGroup.isEmpty())
        {
            LogInAlert.showErrorMessage(null, "Give a name to the group!");
        }else {
            try {
                ArrayList<Long> users_id = (ArrayList<Long>) users.stream().map(x -> x.getId()).collect(Collectors.toList());
                users_id.add(user.getId());
                messageService.createGroup(nameGroup, users_id);
                LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "New Group OK", "Group created successfully!");
            } catch (ValidationException ex) {
                LogInAlert.showErrorMessage(null, ex.getMessage());
            }
        }

    }

    public void handleCleanField(MouseEvent mouseEvent) {
        textFieldName.clear();
    }

    public void handleClose(MouseEvent actionEvent) throws IOException {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/adminPageView.fxml"));
            AnchorPane layout = loader.load();
            System.out.println("---------");
            // Create the dialog Stage.

            String name=user.toString();
            primaryStage.setTitle(name);


            primaryStage.setScene(new Scene(layout));
            AdminController adminController = loader.getController();
            adminController.setService(userService,requestService,messageService,eventService, primaryStage, user,"Message");
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }

    }


    @Override
    public void update(UserChangeEvent messageChangeEvent) {
        initModel();
    }

    public void handlePrevious(ActionEvent actionEvent) {
        pageNumber--;
        initModel();
    }

    public void handleNext(ActionEvent actionEvent) {
        pageNumber++;
        initModel();
    }
}
