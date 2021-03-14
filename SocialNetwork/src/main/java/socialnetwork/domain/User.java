package socialnetwork.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/***
 * creates an object defined by a firstname(String),lastName(String)and a list of friends(List)
 */
public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private List<User> friends;
    private String email;
    private String gender;
    private String password;
    LocalDate birthDate;
    private String notifications;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String firstName, String lastName, String email, String gender, String password, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.password = password;
        this.birthDate = birthDate;
        this.notifications="Off";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    /***
     * Returns the user's first name
     * @return firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /***
     * Sets the user's first name as firstName
     * @param firstName: String
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /***
     * Returns the user's last name
     * @return lastName:String
     */
    public String getLastName() {
        return lastName;
    }

    /***
     * Sets the user's last name as lastName
     * @param lastName:String
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /***
     * Returns the list of friends of the user
     * @return friends:List
     */
    public List<User> getFriends() {
        return friends;
    }


    public String getNotifications() {
        return notifications;
    }

    public void setNotifications(String notifications) {
        this.notifications = notifications;
    }

    /***
     * Returns a string with all the attributes of the user
     * @return String
     */
    @Override
    public String toString() {
        return firstName + " " + lastName + " ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}