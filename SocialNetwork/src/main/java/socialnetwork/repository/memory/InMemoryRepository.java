package socialnetwork.repository.memory;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.serialize.Paged;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E>, Paged<ID,E> {

    private Validator<E> validator;
    protected Map<ID,E> entities;
    Integer pageSize=10;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    @Override
    public E findOne(ID id){
        if (entities.get(id)==null)
            throw new IllegalArgumentException("Nu exista acest id!");
        return entities.get(id);
    }
    @Override
    public boolean exists(ID id){
        if (entities.get(id)==null)
            return false;
        return true;
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    @Override
    public ArrayList<E> getAll() {
        return new ArrayList<E> (entities.values());
    }

    @Override
    public E save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            //return entity;
            throw new ValidationException("Exista deja acest id!");
        }
        else entities.put(entity.getId(),entity);
        return null;
    }

    @Override
    public E delete(ID id) {
        E entity = entities.get(id);
        if(entity == null) {
            throw new ValidationException("Nu exista acest id!");
        }

        entities.remove(id);
        return entity;
    }

    @Override
    public E update(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return entity;

    }


    @Override
    public Iterable<E> getPage(int pageNumber) {
        return entities.values().stream().skip(pageNumber*pageSize).limit(pageSize).collect(Collectors.toList());
    }

    @Override
    public Iterable<E> getPageUser(int pageNumber, Long id) {
        return entities.values().stream().filter(x->x.getId().equals(id)).skip(pageNumber*pageSize).limit(pageSize).collect(Collectors.toList());

    }

    @Override
    public int getPageCount() {
        return entities.size()/pageSize+1;
    }

    @Override
    public int getPageCountUser(Long id) {
        return entities.size()/pageSize+1;
    }
}
