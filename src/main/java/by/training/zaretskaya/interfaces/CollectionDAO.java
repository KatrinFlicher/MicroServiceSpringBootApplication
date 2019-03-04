package by.training.zaretskaya.interfaces;


import by.training.zaretskaya.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Interface with CRUD operations with the entity Collection in DB
 *
 * @author Zaretskaya Katsiaryna
 * @version 1.0
 */
public interface CollectionDAO<C> {
    /**
     * Creates new Collection instance  in Data base.
     *
     * @param collection - Collection instance
     */
    void create(C collection);

    /**
     * Returns Collection instance to which the specified name is mapped from Data base.
     *
     * @param name - name of collection instance
     * @throws ResourceNotFoundException - if collection is not found in Data Base
     */
    C getById(String name);

    /**
     * Removes the mapping collection for a name from Data base if it is present.
     *
     * @param name - name of collection instance
     */
    void delete(String name);

    /**
     * Updates name for the specified name of collection in Data base
     *
     * @param name       - name of collection instance
     * @param collection - new instance of collection with new cache limit and algorithm
     */
    void update(String name, C collection);

    /**
     * Returns list of collections from Data base starting with
     * the specified page and limited in specified size.
     *
     * @param page - number of page
     * @param size - quantity of output collections
     */
    List<C> list(int page, int size);
}
