package socialnetwork.domain.validators;

public interface Validator<T> {

    /***
     * Validates the attributes of the given entity
     * @param entity
     * @throws ValidationException
     *          if the entity is not valid
     */
    void validate(T entity) throws ValidationException;
}