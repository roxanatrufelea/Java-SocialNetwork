package socialnetwork.domain;

import java.util.List;

public class Page {

    private User admin;
    private List<User> friends;
    private List<MessageDTO> receivedMessages;
    private List<RequestDTO> myReceivedRequests;
    private List<Event> comingEvents;

    public Page(User admin, List<User> friends, List<MessageDTO> receivedMessages, List<RequestDTO> myReceivedRequests, List<Event> comingEvents) {
        this.admin = admin;
        this.friends = friends;
        this.receivedMessages = receivedMessages;
        this.myReceivedRequests = myReceivedRequests;
        this.comingEvents = comingEvents;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public List<User> getFriends() {
        return friends;
    }

    public void setFriends(List<User> friends) {
        this.friends = friends;
    }

    public List<MessageDTO> getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(List<MessageDTO> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    public List<RequestDTO> getMyReceivedRequests() {
        return myReceivedRequests;
    }

    public void setMyReceivedRequests(List<RequestDTO> myReceivedRequests) {
        this.myReceivedRequests = myReceivedRequests;
    }

    public List<Event> getComingEvents() {
        return comingEvents;
    }

    public void setComingEvents(List<Event> comingEvents) {
        this.comingEvents = comingEvents;
    }
}
