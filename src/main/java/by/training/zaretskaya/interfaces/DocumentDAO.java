package by.training.zaretskaya.interfaces;


import by.training.zaretskaya.exception.DocumentNotFoundException;

import java.util.List;
/**
 * Interface with CRUD operations with the entity Document in DB
 *
 * @version 1.0
 *
 * @author Zaretskaya Katsiaryna
 * */
public interface DocumentDAO<D> {
    /**
     * Creates new Document instance in the specified table in Data base.
     * @param nameCollection - name of collection
     * @param document - Document instance
     * */
    void create(String nameCollection, D document);

    /**
     * Returns Document instance to which the specified name is mapped from the specified table in Data base.
     * @param nameCollection - name of table collection
     * @param nameResource - name of document instance
     * @throws DocumentNotFoundException - if document is not found in Data Base
     * */
    D get(String nameCollection, String nameResource);

    /**
     * Removes the mapping document for a name from the specified table in Data base if it is present.
     * @param nameCollection - name of table collection
     * @param nameResource - name of document instance
     * */
    void delete(String nameCollection, String nameResource);

    /**
     * Updates value of Document instance for the specified name of document
     * from the specified table in Data base
     * @param nameCollection - name of table collection
     * @param nameResource - name of document instance
     * @param document - undated Document instance
     * */
    void update(String nameCollection, String nameResource, D document);

    /**
     * Returns list of documents for the specified table from Data base starting with
     * the specified page and limited in specified size.
     * @param nameCollection - name of table collection
     * @param page - number of page
     * @param size - quantity of output collections
     * */
    List list(String nameCollection, int page, int size);
}
