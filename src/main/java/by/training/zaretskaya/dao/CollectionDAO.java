package by.training.zaretskaya.dao;


import by.training.zaretskaya.exception.SomethingWrongWithDataBaseException;

import java.util.List;

/**
 * Interface with CRUD operations with the entity Collection in Data Base
 *
 * @author Zaretskaya Katsiaryna
 * @version 1.0
 */
public interface CollectionDAO<C> {
    void create(C collection);

    C getById(String name);

    void delete(String name);

    void update(String name, C collection);

    /**
     * Returns list of collections from Data base compared with
     * the specified object and limited in specified size.
     *
     * @param objectToCompare - object to compare with
     * @param size            - quantity of output collections
     * @throws SomethingWrongWithDataBaseException - if there are some problems with connect to Data Base
     */
    List<C> list(String objectToCompare, int size);

    /**
     * Returns {true} if Data base contains the specified element.
     *
     * @param name - name of collection whose presence in this DB is to be tested
     */
    boolean contains(String name);
}
