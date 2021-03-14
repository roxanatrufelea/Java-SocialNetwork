package socialnetwork.domain.validators;

import socialnetwork.domain.Event;

public class EventValidator implements Validator<Event> {


    @Override
    public void validate(Event entity) throws ValidationException {
        String message = "";
        if(entity.getId()<=0)message+="Id must be a natural number, greater than 0 ! ";
        if (entity.getName().isEmpty()) message+="The name nu cannot be null! ";
        if(message.length()>0) throw new ValidationException(message);
    }

}
