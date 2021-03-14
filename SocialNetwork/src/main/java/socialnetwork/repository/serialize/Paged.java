package socialnetwork.repository.serialize;

import socialnetwork.domain.Entity;

public interface Paged<ID,E extends Entity<ID>> {

    /***
     * Method that returns the page with the specified number
     * @param pageNumber
     * @return the specified page
     */
    Iterable<E> getPage(int pageNumber);

    /***
     * Method that returns the page with the specified number of the user with the given id
     * @param pageNumber
     * @param  id: user id
     * @return the specified page
     */
    Iterable<E> getPageUser(int pageNumber,Long id);

    /**
     * Method that returns the number of pages
     * @return the number of pages
     */
    int getPageCount();
    /**
     * Method that returns the number of pages of entities of the user with given id
     * @return the number of pages
     */
    int getPageCountUser(Long id);

}
