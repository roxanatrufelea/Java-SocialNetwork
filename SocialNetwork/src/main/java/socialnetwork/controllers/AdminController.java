package socialnetwork.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.EventsService;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;
import socialnetwork.utils.ActivityPDF;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class AdminController implements Observer<UserChangeEvent> {
    public Label labelFirstName;
    public Label lableLastName;
    public Label lableEmail;
    public Label lableBirthDate;
    public AnchorPane anchorPaneAbout;
    public Label lableMyName;
    public AnchorPane anchorPaneFriends;
    public Label lablePageF;
    public Button btnNextF;
    public Button btnPreviousF;
    public AnchorPane anchorPaneReports;
    public Button btnDeleteFriend;
    public AnchorPane anchorPaneCreateEvent;
    public TextField textFieldNameEvent;
    public ComboBox cboxHour;
    public ComboBox cboxMinute;
    public AnchorPane anchorPaneGeneralEvent;
    public DatePicker datePicker;
    public Button btnJoin;
    public ListView listViewEvents;
    public TextField textFieldOnOff;
    public TableColumn<RequestDTO, String> tColFName;
    public TableColumn<RequestDTO, String> tColLName;
    public TableColumn<RequestDTO, String> tColDate;

    public Button btnAcceptReq;
    public Button btnRejectReq;
    public TableView <RequestDTO>tableViewReq;
    public AnchorPane anchorPaneRequests;
    public Label lblTextEvents;
    public Button btnGoing;
    public Button btnDeleteEvent;
    public Button btnCancelEvent;
    public TextField txtFieldDescription;
    public TextArea txtAreaEvent;
    public TableColumn<Activity, String> tbColActivity;
    public TableColumn<Activity, String> tbColDate;
    public TableView<Activity> tableViewReport;
    public TextField txtFldDay;
    public TextField txtFldMonth;
    public TextField txtFldYear;
    public AnchorPane anchorPaneMessages;
    public TableColumn<GroupLastDTO, String> tbColNameM;
    public TableColumn<GroupLastDTO, String> tbColFromM;
    public TableColumn<GroupLastDTO, String> tbColMesM;
    public TableColumn<GroupLastDTO, String> tbColDateM;
    public Button btnNewConv;
    public TableView<GroupLastDTO> tableViewMes;
    public AnchorPane anchorPaneConversation;
    public TableView<MessageDTO>tableViewConv;
    public TableColumn<MessageDTO, String> tcolFromC;
    public TableColumn<MessageDTO, String> tColMessafeC;
    public TableColumn<MessageDTO, String> tcolReplyC;
    public TableColumn<MessageDTO, String> tcolDateC;
    public TextArea textAreaMessage;
    public TextField txtFDayM;
    public TextField txtFMonthM;
    public TextField txtFYearM;
    ArrayList<Activity>  activities;
    Group group;

    @FXML
    private TableView<User> tableView;
    @FXML
    TableColumn<User, String> tableColumnFirstName;
    @FXML
    TableColumn<User, String> tableColumnLastName;
    int pageNumberFriends=0;

    ArrayList<Event> today_events;
    String page;
    private FriendshipRequestService requestService;
    private UserService userService;
    private MessageService messageService;
    private EventsService eventService;
    private Stage primaryStage;
    private User user;
    private final ObservableList<User> model = FXCollections.observableArrayList();
    private final ObservableList<Event> modelEvent = FXCollections.observableArrayList();
    private final ObservableList<RequestDTO> modelReq = FXCollections.observableArrayList();
    private final ObservableList<Activity> modelReports = FXCollections.observableArrayList();
    private final ObservableList<GroupLastDTO> modelGroups = FXCollections.observableArrayList();
    private final ObservableList<MessageDTO> modelConversation = FXCollections.observableArrayList();

    Timeline timeline ;

    private void myMethodNotify() {
        String msg="";
//        msg=today_events.stream().filter(x->x.getStart().getMinute()-LocalTime.now().getMinute()==3)
//                .map(x->{return x.getName() + "starts in 10 minutes!";})
//                .toString();
        if(msg.length()>3)LogInAlert.showMessage(null, Alert.AlertType.INFORMATION, "Events of today", msg);


    }

// If you want to repeat indefinitely:



    public void setService(UserService userService, FriendshipRequestService requestService, MessageService messageService,EventsService eventService , Stage stage, User user,String page){
        this.userService=userService;
        this.requestService=requestService;
        this.messageService=messageService;
        this.eventService=eventService;
        this.page=page;
        primaryStage=stage;
        this.user=user;


        lableMyName.setText(user.toString());
        startWithNotif();
        userService.addObserver(this);
        requestService.addObserver(this);
        eventService.addObserver(this);
        messageService.addObserver( this);
        initModel();
        initModelEvent();
        initModelRequest();
        initModelReports();
        initModelMessages();


        today_events= eventService.getMyEventsToday(user.getId());
       group=null;

        for(int i = 1; i<=24; i++)
            cboxHour.getItems().add(i);
        cboxMinute.getItems().addAll(0,15,30,45);


        timeline = new Timeline(
                new KeyFrame(Duration.INDEFINITE, e -> myMethodNotify())

        );
        timeline.setCycleCount(Animation.INDEFINITE);

        timeline.play();
        

    }

    private void initModelConversation() {
        ArrayList<MessageDTO> messageDTOS = (ArrayList<MessageDTO>) messageService.getAllMessagesDTOGroup(group);
        //messageDTOS.forEach(System.out::println);
        modelConversation.setAll(messageDTOS);
    }

    private void initModel() {

        btnDeleteFriend.setVisible(false);
        if(pageNumberFriends>=userService.getFriendsPageCount(user.getId()))pageNumberFriends=userService.getFriendsPageCount(user.getId())-1;
        if(pageNumberFriends<0) pageNumberFriends=0;
        btnNextF.setDisable(pageNumberFriends==userService.getFriendsPageCount(user.getId())-1);
        btnPreviousF.setDisable(pageNumberFriends==0);
        lablePageF.setText("Page"+(pageNumberFriends+1)+"/"+userService.getFriendsPageCount(user.getId()));
         ArrayList<User> users = userService.getFriendsPage(pageNumberFriends,user.getId());
        //List<User> users= StreamSupport.stream(userService.getPage(pageNumberFriends).spliterator(),false).collect(Collectors.toList());
        model.setAll(users);
        

    }
    private void initModelRequest() {

        btnAcceptReq.setVisible(false);
        btnRejectReq.setVisible(false);
        ArrayList<RequestDTO> req = (ArrayList<RequestDTO>) requestService.getUsersRequest(user.getId());
        modelReq.setAll(req);


    }
    private void initModelMessages() {

        ArrayList<GroupLastDTO> groups = (ArrayList<GroupLastDTO>) messageService.getAllGroupLastDTOO(user.getId());
        modelGroups.setAll(groups);


    }

    private void startWithNotif(){
//        System.out.println(page);
//        System.out.println("user notif: "+ user.getNotifications());
//        System.out.println("label notif: "+textFieldOnOff.getText());
        if(page.equals("About")&&userService.getUser(user.getId()).getNotifications().equals("On") && eventService.getMyEventsToday(user.getId()).size()>0) {
            textFieldOnOff.setText("On");
            showEvents();
            LogInAlert.showMessage(null, Alert.AlertType.INFORMATION, "Events of today", "You have events today!Go check out!");


        }

        else {
            textFieldOnOff.setText("Off");
            if(page.equals("Friends")) showFriends();
            else if(page.equals("Message")) showChat();
            else showAbout();
        }
    }

    
    private void initModelEvent()
    {
       today_events= eventService.getMyEventsToday(user.getId());
        modelEvent.setAll(today_events);
    }
    private void initModelReports() {

        activities = messageService.receivedMessagesToActivityUser(user.getId());
        activities.addAll(userService.friendshipsActivityOfUser(user.getId()));
        activities= (ArrayList<Activity>) activities.stream().sorted((x, y)->x.getDate().compareTo(y.getDate()))
                .collect(Collectors.toList());
        modelReports.setAll(activities);
    }

    @FXML
    private void initialize(){



        tableView.setItems(model);
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User,String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User,String>("lastName"));
        tableView.setOnMouseClicked(e->handleBtnDeleteFrienship());


        listViewEvents.setItems(modelEvent);

        listViewEvents.setCellFactory(param -> new ListCell<Event>() {
            @Override
            protected void updateItem(Event item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item.getName() == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
            }
        });
        listViewEvents.setOnMouseClicked(e->handleSelectFromList());
        listViewEvents.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        tableViewReq.setItems(modelReq);
        tColFName.setCellValueFactory(new PropertyValueFactory<RequestDTO,String>("firstName"));
        tColLName.setCellValueFactory(new PropertyValueFactory<RequestDTO,String>("lastName"));
        tColDate.setCellValueFactory(new PropertyValueFactory<RequestDTO,String>("date"));
        tableViewReq.setOnMouseClicked(e->handleAcceptRejectRequest());


        tableViewReport.setItems(modelReports);
        tbColActivity.setCellValueFactory(new PropertyValueFactory<Activity,String>("activity"));
        tbColDate.setCellValueFactory(new PropertyValueFactory<Activity,String>("date"));



        tableViewMes.setItems(modelGroups);
        tbColNameM.setCellValueFactory(new PropertyValueFactory<GroupLastDTO,String>("name"));
        tbColFromM.setCellValueFactory(new PropertyValueFactory<GroupLastDTO,String>("user"));
        tbColMesM.setCellValueFactory(new PropertyValueFactory<GroupLastDTO,String>("message"));
        tbColDateM.setCellValueFactory(new PropertyValueFactory<GroupLastDTO,String>("date"));
        tableViewMes.setOnMouseClicked(e->handleConversation());


        tableViewConv.setItems(modelConversation);
        tcolFromC.setCellValueFactory(new PropertyValueFactory<MessageDTO,String>("from"));
        tColMessafeC.setCellValueFactory(new PropertyValueFactory<MessageDTO,String>("message"));
        tcolDateC.setCellValueFactory(new PropertyValueFactory<MessageDTO,String>("date"));
        tcolReplyC.setCellValueFactory(new PropertyValueFactory<MessageDTO,String>("reply"));

    }

    private void handleConversation() {



        GroupLastDTO u = tableViewMes.getSelectionModel().getSelectedItem();
        if (u == null) {
            LogInAlert.showErrorMessage(null, "You haven't selected any conversation!");
        } else {
            try {
                group=messageService.getGroup(u.getId());
                initModelConversation();
                anchorPaneMessages.setVisible(false);
                anchorPaneRequests.setVisible(false);
                anchorPaneAbout.setVisible(false);
                anchorPaneCreateEvent.setVisible(false);

                anchorPaneGeneralEvent.setVisible(false);
                anchorPaneFriends.setVisible(false);
                anchorPaneReports.setVisible(false);
                anchorPaneConversation.setVisible(true);


            } catch (ValidationException ex) {
                LogInAlert.showErrorMessage(null, ex.getMessage());
            }


        }
    }
    private void handleSelectFromList() {
        anchorPaneCreateEvent.setVisible(false);
        txtAreaEvent.setVisible(true);
        if(btnDeleteEvent.isVisible())btnDeleteEvent.setDisable(false);
        if(btnCancelEvent.isVisible())btnCancelEvent.setDisable(false);
        if(btnJoin.isVisible())btnJoin.setDisable(false);
        Event event= (Event) listViewEvents.getSelectionModel().getSelectedItem();
        String ev="Name: "+event.getName()+"\n"+"Date: "+ event.getDate()+"\n"+"Time: "+event.getStart()+"\n"+"Description: "+event.getDescription()+"\n"+"Created by: "+userService.findUser(event.getAdmin()).toString();
        txtAreaEvent.setText(ev);
    }

    private void handleBtnDeleteFrienship() {
        btnDeleteFriend.setVisible(true);
    }

    private void handleAcceptRejectRequest() {
        btnAcceptReq.setVisible(true);
        btnRejectReq.setVisible(true);
    }

    @Override
    public void update(UserChangeEvent userChangeEvent) {
        initModel();
        initModelEvent();
        initModelRequest();
        initModelMessages();
        if(group!=null)initModelConversation();
        today_events= eventService.getMyEventsToday(user.getId());

    }


    public void handleReports(ActionEvent actionEvent) {
        anchorPaneMessages.setVisible(false);
        anchorPaneRequests.setVisible(false);
        anchorPaneAbout.setVisible(false);
        anchorPaneCreateEvent.setVisible(false);
        anchorPaneConversation.setVisible(false);
        anchorPaneGeneralEvent.setVisible(false);
        anchorPaneFriends.setVisible(false);
        anchorPaneReports.setVisible(true);

    }
    public void showFriends()
    {
        anchorPaneMessages.setVisible(false);
        anchorPaneRequests.setVisible(false);
        anchorPaneAbout.setVisible(false);
        anchorPaneReports.setVisible(false);
        anchorPaneCreateEvent.setVisible(false);
        anchorPaneConversation.setVisible(false);
        anchorPaneGeneralEvent.setVisible(false);
        anchorPaneFriends.setVisible(true);
    }

    public void handleFriends(ActionEvent actionEvent) {
        showFriends();
    }

    public void handleAbout(ActionEvent actionEvent) {
        showAbout();
    }
    public void showAbout()
    {
        anchorPaneMessages.setVisible(false);
        anchorPaneRequests.setVisible(false);
        anchorPaneFriends.setVisible(false);
        anchorPaneConversation.setVisible(false);
        anchorPaneGeneralEvent.setVisible(false);
        anchorPaneReports.setVisible(false);
        anchorPaneCreateEvent.setVisible(false);
        anchorPaneAbout.setVisible(true);
        labelFirstName.setText(user.getFirstName());
        lableLastName.setText(user.getLastName());
        lableEmail.setText(user.getEmail());
        lableBirthDate.setText(user.getBirthDate().toString());
    }

    public void handleEvents(ActionEvent actionEvent) {
        showEvents();
    }

    public void showEvents()
    {
        btnCancelEvent.setVisible(false);
        btnDeleteEvent.setVisible(false);
        btnJoin.setVisible(false);
        anchorPaneRequests.setVisible(false);
        anchorPaneFriends.setVisible(false);
        anchorPaneCreateEvent.setVisible(false);
        anchorPaneConversation.setVisible(false);
        anchorPaneReports.setVisible(false);
        anchorPaneMessages.setVisible(false);
        anchorPaneAbout.setVisible(false);
        anchorPaneGeneralEvent.setVisible(true);
    }

    public void handleLogOut(ActionEvent actionEvent) {
        primaryStage.close();
    }

    public void handleReload(MouseEvent mouseEvent) {
    }


    public void showChat()
    {
        anchorPaneRequests.setVisible(false);
        anchorPaneFriends.setVisible(false);
        anchorPaneCreateEvent.setVisible(false);
        anchorPaneReports.setVisible(false);
        anchorPaneConversation.setVisible(false);
        anchorPaneAbout.setVisible(false);
        anchorPaneGeneralEvent.setVisible(false);
        anchorPaneMessages.setVisible(true);
    }
    public void handleChat(MouseEvent mouseEvent) {
        showChat();




//        try{
//            FXMLLoader loader = new FXMLLoader();
//            loader.setLocation(getClass().getResource("/views/messageView.fxml"));
//            AnchorPane layout = loader.load();
//            // Create the dialog Stage.
//
//            primaryStage.setTitle("Groups");
//
//
//            primaryStage.setScene(new Scene(layout));
//            MessageController messageController = loader.getController();
//            messageController.setService(userService,requestService,messageService,eventService, primaryStage, user);
//            primaryStage.show();
//        }catch(IOException e){
//            e.printStackTrace();
//        }



    }

    public void handlePrevious() {
        pageNumberFriends--;
        initModel();
    }

    public void handleNext() {
        pageNumberFriends++;
        initModel();
    }

    public void handleFriendshipRequest(ActionEvent actionEvent) {
        anchorPaneFriends.setVisible(false);
        anchorPaneCreateEvent.setVisible(false);
        anchorPaneReports.setVisible(false);
        anchorPaneAbout.setVisible(false);
        anchorPaneGeneralEvent.setVisible(false);
        anchorPaneConversation.setVisible(false);
        anchorPaneRequests.setVisible(true);


    }



    public void handleYes(ActionEvent actionEvent) {
        userService.removeUser(user.getId());
        primaryStage.close();
    }

    public void handleNo(ActionEvent actionEvent) {
        anchorPaneReports.setVisible(false);
    }

    @FXML
    public void handleDeleteFriend(ActionEvent actionEvent) {
        User u= tableView.getSelectionModel().getSelectedItem();
        if(u == null) {
            LogInAlert.showErrorMessage(null, "You haven't selected any friend!");
        }
        else {
            try {
               // System.out.println(u.getId()+" "+u.toString());
                userService.removeFriendship(user.getId(), u.getId());
                requestService.deleteFriendshipRequest(user.getId(), u.getId());
                LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Delete OK", "Friend removed successfully!");
                initModel();
            }catch(ValidationException ex) {
                LogInAlert.showErrorMessage(null, ex.getMessage());
            }
        }

    }

    public void handleNewFriends(ActionEvent actionEvent) {
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/sendRequestView.fxml"));
            AnchorPane layout = loader.load();
            // Create the dialog Stage.

            primaryStage.setTitle("Let's make new friends!");


            primaryStage.setScene(new Scene(layout));
            SendRequestController sendRequestController = loader.getController();
            sendRequestController.setService(userService,requestService,messageService,eventService, primaryStage, user);
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void handleToday(ActionEvent actionEvent) {
        lblTextEvents.setText("Today");
        btnJoin.setVisible(false);
        btnDeleteEvent.setVisible(false);
        btnCancelEvent.setVisible(true);
        btnCancelEvent.setDisable(true);
        today_events= eventService.getMyEventsToday(user.getId());
        modelEvent.setAll(today_events);
    }

    public void handleAllEvents(ActionEvent actionEvent) {
        lblTextEvents.setText("All Events");
        btnDeleteEvent.setVisible(false);
        btnCancelEvent.setVisible(false);
        btnJoin.setVisible(true);
        btnJoin.setDisable(true);
        ArrayList<Event> today_events= eventService.getAllEvents();
        modelEvent.setAll(today_events);

    }

    public void handleMyEvents(ActionEvent actionEvent) {

        btnCancelEvent.setVisible(false);
        btnJoin.setVisible(false);
        btnDeleteEvent.setVisible(true);
        btnDeleteEvent.setDisable(true);
        lblTextEvents.setText("My Events");
        ArrayList<Event> today_events= eventService.myEvents(user.getId());
        modelEvent.setAll(today_events);
    }

    public void handleCreate(ActionEvent actionEvent) {
        txtAreaEvent.setVisible(false);
        anchorPaneRequests.setVisible(false);
        anchorPaneFriends.setVisible(false);
        anchorPaneReports.setVisible(false);
        anchorPaneAbout.setVisible(false);
        anchorPaneConversation.setVisible(false);
        anchorPaneGeneralEvent.setVisible(true);
        anchorPaneCreateEvent.setVisible(true);

    }

    public void handleCreateEvent(ActionEvent actionEvent) {

        anchorPaneCreateEvent.setVisible(true);
        try {

            String name = textFieldNameEvent.getText();
            LocalDate date = datePicker.getValue();
            Integer hour = (Integer) cboxHour.getValue();
            Integer minute = (Integer) cboxMinute.getValue();
            LocalTime time = LocalTime.of(hour, minute);
            String description=txtFieldDescription.getText();
            try {
                eventService.addEvent(name, date, time, user.getId(),description);
                LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Add OK", "Event created successfully!");
                textFieldNameEvent.clear();
                txtFieldDescription.clear();
                datePicker.getEditor().clear();
                cboxMinute.valueProperty().set("Min");
                cboxHour.valueProperty().set("Hour");
                anchorPaneCreateEvent.setVisible(false);

            } catch (ValidationException ex) {
                LogInAlert.showErrorMessage(null, ex.getMessage());
            }
        } catch (NullPointerException ex) {

            LogInAlert.showErrorMessage(null,"Every field must be completed!");

        }

    }

    public void handleJoin(ActionEvent actionEvent) {

        Event event= (Event) listViewEvents.getSelectionModel().getSelectedItem();
        //System.out.println("Event:  "+event);
        if(event==null)
        {
            LogInAlert.showErrorMessage(null,"You must select an event!");
        }
        else
        {
            try {
                eventService.joinEvent(event, user.getId());
                LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Join OK", "Event joined successfully!");
            }catch (ValidationException ex)
            {
                LogInAlert.showErrorMessage(null,ex.getMessage());
            }
        }

    }





    public void handleOnOff(MouseEvent actionEvent) {

        if(textFieldOnOff.getText().equals("On")) {
            textFieldOnOff.setText("Off");
            userService.updateUserNotifications(user.getId(),"Off");
        }
        else {
            textFieldOnOff.setText("On");
            userService.updateUserNotifications(user.getId(),"On");
        }
    }

    public void handleAcceptFriendship(ActionEvent actionEvent) {
        RequestDTO requestDTO= (RequestDTO) tableViewReq.getSelectionModel().getSelectedItem();
        if(requestDTO== null) {
            LogInAlert.showErrorMessage(null, "You haven't selected anybody!");
        }
        else {
            try {
                requestService.acceptRequest(user.getId(),requestDTO.getId());
                LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Accept request OK", "You accepted the friendship request!");
            }catch(ValidationException ex) {
                LogInAlert.showErrorMessage(null, ex.getMessage());
            }
        }
    }

    public void handleRejectReq(ActionEvent actionEvent) {
        RequestDTO requestDTO= tableViewReq.getSelectionModel().getSelectedItem();
        if(requestDTO == null) {
            LogInAlert.showErrorMessage(null, "You haven't selected anybody!");
        }
        else {
            try {
                requestService.rejectRequest(user.getId(),requestDTO.getId());
                LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Refuse request OK", "You refused the friendship request!");
            }catch(ValidationException ex) {
                LogInAlert.showErrorMessage(null, ex.getMessage());
            }
        }
    }

    public void handleGoing(ActionEvent actionEvent) {
        btnJoin.setVisible(false);
        btnDeleteEvent.setVisible(false);
        btnCancelEvent.setVisible(true);
        btnCancelEvent.setDisable(true);
        lblTextEvents.setText("Going");
        ArrayList<Event> going_events= eventService.getGoingEvents(user.getId());
        modelEvent.setAll(going_events);
    }

    public void handleDeleteEvent(ActionEvent actionEvent) {

        Event event= (Event) listViewEvents.getSelectionModel().getSelectedItem();
        if(event==null)
        {
            LogInAlert.showErrorMessage(null,"You must select an event!");
        }
        else
        {
            try {
                eventService.deleteEvent(event.getId(), user.getId());
                LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Delete OK", "Event deleted successfully!");
                txtAreaEvent.setVisible(false);
            }catch (ValidationException ex)
            {
                LogInAlert.showErrorMessage(null,ex.getMessage());
            }
        }
    }

    public void handleCancelEvent(ActionEvent actionEvent) {
        Event event= (Event) listViewEvents.getSelectionModel().getSelectedItem();
        if(event==null)
        {
            LogInAlert.showErrorMessage(null,"You must select an event!");
        }
        else
        {
            try {
                eventService.unjoinEvent(event, user.getId());
                LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Cancel OK", "Event canceled successfully!");
            }catch (ValidationException ex)
            {
                LogInAlert.showErrorMessage(null,ex.getMessage());
            }
        }
    }

    public void handlePDF(ActionEvent actionEvent) {
        ArrayList<Activity> activities= new ArrayList<>();
        activities.addAll(modelReports);

        ActivityPDF activityPDF=new ActivityPDF(user.toString(),"My activity", (ArrayList<Activity>) activities);
        activityPDF.generatePDF();
        LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "PDF OK", "The report has been successfully!");

    }

    public void handleFilterReport(KeyEvent keyEvent) {
        modelReports.setAll(activities
                .stream()
                .filter(x -> {
                    // day
                    String day = txtFldDay.getText();
                    if(day.isEmpty()) {

                        return true;
                    }
                    return String.valueOf(x.getDate().getDayOfMonth()).equals(day);

                })
                .filter(x -> {
                    //month
                    String month = txtFldMonth.getText();
                    if(month.isEmpty()) {

                        return true;
                    }
                    return (x.getDate().getMonth().toString().toLowerCase().startsWith(month.toLowerCase())||String.valueOf(x.getDate().getMonthValue()).startsWith(month));

                })
                .filter(x -> {
                    //year
                    String year = txtFldYear.getText();
                    if(year.isEmpty()) {

                        return true;
                    }
                    return String.valueOf(x.getDate().getYear()).startsWith(year);
                })
                .collect(Collectors.toList())
        );

    }

    public void handleNewConv(ActionEvent actionEvent) {


        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/sendMessageView.fxml"));
            AnchorPane layout = loader.load();
            // Create the dialog Stage.

            primaryStage.setTitle("New conversation");


            primaryStage.setScene(new Scene(layout));
            SendMessageController sendMessageController = loader.getController();
            sendMessageController.setService(userService,requestService,messageService, eventService, primaryStage, user);
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();
        }catch(ValidationException ex) {
            LogInAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    public void handleSendMes(ActionEvent actionEvent) {

        try {
            String messageText = textAreaMessage.getText();
            ArrayList<Long> users= (ArrayList<Long>) group.getUsers().clone();
            messageService.sendMessageGroup(group.getId(), user.getId(), users,messageText);
        }catch (ValidationException ex){
            LogInAlert.showErrorMessage(null, ex.getMessage());
        }
        textAreaMessage.clear();

    }

    public void handleReplyM(ActionEvent actionEvent) {
        MessageDTO u = tableViewConv.getSelectionModel().getSelectedItem();
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

    public void handlePDFMess(ActionEvent actionEvent) {

        ArrayList<Activity> activities=messageService.receivedMessagesDTO( modelConversation);
        String title="";
        if (group.getUsers().size()>2) title=group.getName();
        else
        {
            for(Long id: group.getUsers())if(id!= user.getId()) title=userService.find(id).toString();
        }
        ActivityPDF activityPDF=new ActivityPDF(user.toString(),"Messages from "+title,activities);
        activityPDF.generatePDF();
        LogInAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "PDF OK", "The report has been successfully!");





    }

    public void handleConvUsers(ActionEvent actionEvent) {
        LogInAlert.showMessage(null, Alert.AlertType.INFORMATION, "Participants",messageService.getParticipantsGroup(group) );

    }

    public void filterConversation(KeyEvent keyEvent) {
        modelConversation.setAll(messageService.getAllMessagesDTOGroup(group)
                .stream()
                .filter(x -> {
                    // day
                    String day = txtFDayM.getText();
                    if(day.isEmpty()) {

                        return true;
                    }
                    return String.valueOf(x.getDate().getDayOfMonth()).equals(day);

                })
                .filter(x -> {
                    //month
                    String month = txtFMonthM.getText();
                    if(month.isEmpty()) {

                        return true;
                    }
                    return (x.getDate().getMonth().toString().toLowerCase().startsWith(month.toLowerCase())||String.valueOf(x.getDate().getMonthValue()).startsWith(month));

                })
                .filter(x -> {
                    //year
                    String year = txtFYearM.getText();
                    if(year.isEmpty()) {

                        return true;
                    }
                    return String.valueOf(x.getDate().getYear()).startsWith(year);
                })
                .collect(Collectors.toList())
        );
    }

    public void handleCloseConversation(MouseEvent mouseEvent) {
        anchorPaneRequests.setVisible(false);
        anchorPaneFriends.setVisible(false);
        anchorPaneCreateEvent.setVisible(false);
        anchorPaneReports.setVisible(false);
        anchorPaneConversation.setVisible(false);
        anchorPaneAbout.setVisible(false);
        anchorPaneGeneralEvent.setVisible(false);
        anchorPaneMessages.setVisible(true);

    }
}
