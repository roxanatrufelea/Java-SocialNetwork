package socialnetwork.domain;

import java.util.ArrayList;

public class Group extends Entity<Long> {
    private String name;
    private ArrayList <Long> users;
    private ArrayList<Long> messages;
    private User admin;

    public Group(Long idi,String name, ArrayList<Long> users, ArrayList<Long> messages) {
        this.id=idi;
        this.name = name;
        this.users = users;
        this.messages = messages;
    }
    public Group(Long idi,String name, ArrayList<Long> users) {
        this.id=idi;
        this.name = name;
        this.users = users;
        messages=new ArrayList<Long>();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Long> getUsers() {
        return (ArrayList<Long>) users.clone();
    }

    public void setUsers(ArrayList<Long> users) {
        this.users = users;
    }

    public ArrayList<Long> getMessages() {
        return (ArrayList<Long>) messages.clone();
    }

    public void setMessages(ArrayList<Long> messages) {
        this.messages = messages;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + name + '\'' +
                ", users=" + users +
                ", messages=" + messages +
                ", admin=" + admin +
                '}';
    }
}
