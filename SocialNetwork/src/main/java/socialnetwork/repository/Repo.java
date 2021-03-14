package socialnetwork.repository;

import socialnetwork.domain.Entity;
import socialnetwork.repository.serialize.Paged;

public interface Repo<ID,E extends Entity<ID>> extends Repository<ID,E>, Paged<ID,E> {
}
