package socialnetwork.domain.validators;

import socialnetwork.domain.FriendshipRequest;


public class FriendshipRequestValidator implements Validator<FriendshipRequest>{

    @Override
    public void validate(FriendshipRequest entity) throws ValidationException {
        if(entity.getId().getRight()==entity.getId().getLeft()){
            throw new ValidationException("Utilizatorii trebuie sa fie diferiti!");

        }

    }

    /***
     * Validates the parsing of a string to long
     * @param nr:String
     * @return long(nr)
     * @throws ValidationException if the string can't be turned to a number
     */
    public static Long validLong(String nr){
        try {
            return Long.parseLong(nr);
        }catch (NumberFormatException ex){
            throw new ValidationException(nr +"nu este un numar valid! ");
        }
    }
}
