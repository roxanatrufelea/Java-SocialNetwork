package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.GroupLastDTO;
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

public class MessageController implements Observer<UserChangeEvent> {
    public Button btnPrevious;
    public Button btnNext;
    public Label labelPageNumber;
    @FXML
    TableView<GroupLastDTO> tableView;
    @FXML
    TableColumn<GroupLastDTO, String> tableColumnName;
    @FXML
    TableColumn<GroupLastDTO, String> tableColumnFrom;
    @FXML
    TableColumn<GroupLastDTO, String> tableColumnMessage;
    @FXML
    TableColumn<GroupLastDTO, String> tableColumnDate;

    private MessageService messageService;
    private FriendshipRequestService requestService;
    private UserService userService;
    EventsService eventService;
    private Stage stage;
    private User user;
    private ObservableList<GroupLastDTO> model = FXCollections.observableArrayList();
    int pageNumber=0;

    public void setService(UserService userService,FriendshipRequestService requestService, MessageService messageService, EventsService eventService,Stage stage, User user) {
        this.messageService = messageService;
        this.requestService=requestService;
        this.userService = userService;
        this.eventService=eventService;
        this.user = user;
        this.stage = stage;
        messageService.addObserver(this);
        initModel();
    }

    private void initModel() {

        if(pageNumber>=messageService.getGroupPageCountUser(user.getId()))pageNumber=messageService.getGroupPageCountUser(user.getId())-1;
        if(pageNumber<0) pageNumber=0;
        btnNext.setDisable(pageNumber==messageService.getGroupPageCountUser(user.getId())-1);
        btnPrevious.setDisable(pageNumber==0);
        labelPageNumber.setText("Page"+(pageNumber+1)+"/"+messageService.getGroupPageCountUser(user.getId()));


        ArrayList<GroupLastDTO> groups = (ArrayList<GroupLastDTO>) messageService.getAllGroupLastDTO(pageNumber,user.getId());
      //  groups.forEach(System.out::println);
        model.setAll(groups);
    }

    @FXML
    private void initialize(){

        tableView.setItems(model);
        tableColumnName.setCellValueFactory(new PropertyValueFactory<GroupLastDTO,String>("name"));
        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<GroupLastDTO,String>("user"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<GroupLastDTO,String>("message"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<GroupLastDTO,String>("date"));
        tableView.setOnMouseClicked(e->handleConversation());

    }
    @FXML
    public void handleConversation() {
        GroupLastDTO u = tableView.getSelectionModel().getSelectedItem();
        if (u == null) {
            LogInAlert.showErrorMessage(null, "You haven't selected any conversation!");
        } else {
            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/conversationView.fxml"));
                AnchorPane layout = loader.load();
                // Create the dialog Stage.
                stage.setTitle(u.getName());


                stage.setScene(new Scene(layout));
                ConversationController conversationController = loader.getController();
                conversationController.setService(userService,requestService,messageService,eventService,stage, user,messageService.getGroup(u.getId()));
                stage.show();
            }catch(IOException e){
                e.printStackTrace();
            }catch(ValidationException ex) {
                LogInAlert.showErrorMessage(null, ex.getMessage());
            }


        }
    }


    public void handleNewConversation(ActionEvent actionEvent) {

        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/sendMessageView.fxml"));
            AnchorPane layout = loader.load();
            // Create the dialog Stage.

            stage.setTitle("New conversation");


            stage.setScene(new Scene(layout));
            SendMessageController sendMessageController = loader.getController();
            sendMessageController.setService(userService,requestService,messageService, eventService, stage, user);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }catch(ValidationException ex) {
            LogInAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    public void handleClose(MouseEvent actionEvent) {
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
            adminController.setService(userService,requestService,messageService,eventService, stage, user,"Message");
            stage.show();
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
