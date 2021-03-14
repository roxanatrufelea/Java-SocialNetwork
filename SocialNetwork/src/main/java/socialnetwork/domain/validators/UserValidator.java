package socialnetwork.domain.validators;

import socialnetwork.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String message = "";
        if(entity.getId()<=0)message+="Id-ul trebuie sa fie un numar natural, diferit de 0! ";
        if (entity.getFirstName().isEmpty()) message+="Numele nu poate fi vid! ";
        if(entity.getLastName().isEmpty()) message+="Prenumele nu poate fi vid! ";
        if(!entity.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            message+="Invalid email! ";
        }
        if(entity.getPassword().length()<8)message+="Password must be at least 8 characters long";
        if(message.length()>0) throw new ValidationException(message);
    }
}
