package by.training.zaretskaya.dao;


import by.training.zaretskaya.exception.SomethingWrongWithDataBaseException;

import java.util.List;

/**
 * Interface with CRUD operations with the entity Document in DB
 *
 * @author Zaretskaya Katsiaryna
 * @version 1.0
 */
public interface DocumentDAO<D> {
    void create(String nameCollection, D document);

    D get(String nameCollection, String nameResource);

    void delete(String nameCollection, String nameResource);

    void update(String nameCollection, String nameResource, D document);

    /**
     * Returns list of documents for the specified table from Data base starting with
     * the specified page and limited in specified size.
     *
     * @param nameCollection  - name of table collection
     * @param objectToCompare - object to compare with
     * @param size            - quantity of output collections
     * @throws SomethingWrongWithDataBaseException - if there are some problems with connect to Data Base
     */
    List<D> list(String nameCollection, String objectToCompare, int size);

    /**
     * Returns {true} if Data base contains the specified element.
     *
     * @param nameCollection - name of table collection
     * @param nameResource   - name of document instance
     */
    boolean contains(String nameCollection, String nameResource);
}
