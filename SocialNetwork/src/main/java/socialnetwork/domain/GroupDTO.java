package socialnetwork.domain;

import java.util.ArrayList;

public class GroupDTO extends Entity<Long>{
    private String name;
    private ArrayList<User> users;
    private ArrayList<Message> messages;

    public GroupDTO(Long idi,String name, ArrayList<User> users, ArrayList<Message> messages) {

        this.id=idi;
        this.name = name;
        this.users = users;
        this.messages = messages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
    public String getLastUser()
    {
        return  messages.get(messages.size()-1).getFrom().toString();
    }
    public String getLastMessage()
    {
        return  messages.get(messages.size()-1).getMessage();
    }

}
