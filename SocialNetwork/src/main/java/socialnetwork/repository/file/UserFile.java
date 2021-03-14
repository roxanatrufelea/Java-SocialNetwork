package socialnetwork.repository.file;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class UserFile extends AbstractFileRepository<Long, User>{

    public UserFile(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }

    /***
     * Creates an Utilizator object with the attributes from the given list of string
     * @param attributes:List<String>
     * @return user:Utilizator
     */
    @Override
    public User extractEntity(List<String> attributes) {

        User user = new User(attributes.get(1),attributes.get(2));
        user.setId(Long.parseLong(attributes.get(0)));

        return user;
    }

    /***
     * Creates a String with the attributes of the given user
     * @param entity:Utilizator
     * @return String
     */
    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }
}
