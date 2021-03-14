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
import socialnetwork.domain.Activity;
import socialnetwork.domain.Group;
import socialnetwork.domain.MessageDTO;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.EventsService;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;
import socialnetwork.utils.ActivityPDF;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ConversationController implements Observer<UserChangeEvent> {

    public TextField textFieldDay;
    public TextField textFieldMonth;
    public TextField textFieldYear;
    @FXML
    TableView<MessageDTO> tableView;
    @FXML
    TableColumn<MessageDTO, String> tableColumnReply;
    @FXML
    TableColumn<MessageDTO, String> tableColumnFrom;
    @FXML
    TableColumn<MessageDTO, String> tableColumnMessage;
    @FXML
    TableColumn<MessageDTO, String> tableColumnDate;
    @FXML
    TextArea textAreaMessage;

    private MessageService messageService;
    private FriendshipRequestService requestService;
    private UserService userService;
    EventsService eventService;
    private Stage stage;
    private User user;
    private Group group;
    private ObservableList<MessageDTO> model = FXCollections.observableArrayList();

    public void setService(UserService userService,FriendshipRequestService requestService, MessageService messageService, EventsService eventService,Stage stage, User user,Group group) {
        this.messageService = messageService;
        this.requestService=requestService;
        this.userService = userService;
        this.eventService=eventService;
        this.user = user;
        this.stage = stage;
        this.group=group;
        messageService.addObserver( this);
        initModel();
    }

    private void initModel() {
        ArrayList<MessageDTO> messageDTOS = (ArrayList<MessageDTO>) messageService.getAllMessagesDTOGroup(group);
        messageDTOS.forEach(System.out::println);
        model.setAll(messageDTOS);
    }

    @FXML
    private void initialize(){

        tableView.setItems(model);
        tableColumnFrom.setCellValueFactory(new PropertyValueFactory<MessageDTO,String>("from"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<MessageDTO,String>("message"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<MessageDTO,String>("date"));
        tableColumnReply.setCellValueFactory(new PropertyValueFactory<MessageDTO,String>("reply"));

    }
    public void textFieldFilter(KeyEvent keyEvent) {

        model.setAll(messageService.getAllMessagesDTOGroup(group)
                .stream()
                .filter(x -> x.getFrom().getId()!=user.getId())
                .filter(x -> {
                    // day
                    String day = textFieldDay.getText();

                    if(day.equals("Day")) {
                        System.out.println("fara zi");
                        return true;
                    }
                    return String.valueOf(x.getDate().getDayOfMonth()).startsWith(day);

                })
                .filter(x -> {
                    //month
                    String month = textFieldMonth.getText();

                    if(month.equals("Month")) {
                        System.out.println("fara luna");
                        return true;
                    }
                    return (x.getDate().getMonth().toString().startsWith(month)||String.valueOf(x.getDate().getMonthValue()).startsWith(month));

                })
                .filter(x -> {
                    //year
                    String year = textFieldYear.getText();

                    if(year.equals("Year")) {
                        System.out.println("fara an");
                        return true;
                    }
                    return String.valueOf(x.getDate().getYear()).startsWith(year);
                })
                .collect(Collectors.toList())
        );

    }

    public void handleClose(MouseEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/messageView.fxml"));
            AnchorPane layout = loader.load();
            // Create the dialog Stage.

            stage.setTitle("Groups");


            stage.setScene(new Scene(layout));
            MessageController messageController = loader.getController();
            messageController.setService(userService,requestService,messageService,eventService,stage, user);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void handlesendMessage(ActionEvent actionEvent) {
        try {
            String messageText = textAreaMessage.getText();
            ArrayList<Long> users= (ArrayList<Long>) group.getUsers().clone();
            messageService.sendMessageGroup(group.getId(), user.getId(), users,messageText);
        }catch (ValidationException ex){
            LogInAlert.showErrorMessage(null, ex.getMessage());
        }
        textAreaMessage.clear();

    }

    public void handleReply()
    {
        MessageDTO u = tableView.getSelectionModel().getSelectedItem();
        if (u == null) {
            LogInAlert.showErrorMessage(null, "You haven't selected any message!");
        } else
            {
                try {
                    String messageText = textAreaMessage.getText();
                    messageService.replyGroupMessage(group.getId(), u.getId(),messageText);
                }catch (ValidationException ex){
                    LogInAlert.showErrorMessage(null, ex.getMessage());
                }
                textAreaMessage.clear();
            }
    }


    @Override
    public void update(UserChangeEvent messageChangeEvent) {
        initModel();
    }

    public void handleBack(ActionEvent actionEvent) {
        initModel();
    }

    public void handleCleanFieldDay(MouseEvent mouseEvent) {
        textFieldDay.clear();
    }

    public void handleCleanFieldMonth(MouseEvent mouseEvent) {
        textFieldMonth.clear();
    }

    public void handleCleanFieldYear(MouseEvent mouseEvent) {
        textFieldYear.clear();
    }

    public void handlePDF(ActionEvent actionEvent) {

        ArrayList<Activity> activities=messageService.receivedMessagesDTO( model);
        String title="";
        if (group.getUsers().size()>2) title=group.getName();
        else
        {
            for(Long id: group.getUsers())if(id!= user.getId()) title=userService.find(id).toString();
        }
        ActivityPDF activityPDF=new ActivityPDF(user.toString(),"Messages from "+title,activities);
        activityPDF.generatePDF();




    }

    public void handleParticipants(ActionEvent actionEvent) {

        LogInAlert.showMessage(null, Alert.AlertType.INFORMATION, "Participants",messageService.getParticipantsGroup(group) );
    }
}

