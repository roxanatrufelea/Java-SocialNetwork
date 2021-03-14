package socialnetwork.domain.validators;

import socialnetwork.domain.Message;
import socialnetwork.domain.User;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        String error = "";
        if(entity.getId()<=0)error+="Id must be natural number >0! ";
        if (entity.getFrom()<=0) error+="An user id must be natural number >0! ";
        if (entity.getTo().isEmpty()) error+="A message must be sent to somebody ";
        if(error.length()>0) throw new ValidationException(error);
    }
}
