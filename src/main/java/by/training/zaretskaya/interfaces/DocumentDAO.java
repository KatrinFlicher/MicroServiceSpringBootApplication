package by.training.zaretskaya.interfaces;


import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.exception.SomethingWrongWithDataBaseException;

import java.util.List;

/**
 * Interface with CRUD operations with the entity Document in DB
 *
 * @author Zaretskaya Katsiaryna
 * @version 1.0
 */
public interface DocumentDAO<D> {
    /**
     * Creates new Document instance in the specified table in Data base.
     *
     * @param nameCollection - name of collection
     * @param document       - Document instance
     * @throws SomethingWrongWithDataBaseException - if there are some problems with connect to Data Base
     */
    void create(String nameCollection, D document);

    /**
     * Returns Document instance to which the specified name is mapped from the specified table in Data base.
     *
     * @param nameCollection - name of table collection
     * @param nameResource   - name of document instance
     * @throws ResourceNotFoundException           - if document is not found in Data Base
     * @throws SomethingWrongWithDataBaseException - if there are some problems with connect to Data Base
     */
    D get(String nameCollection, String nameResource);

    /**
     * Removes the mapping document for a name from the specified table in Data base if it is present.
     *
     * @param nameCollection - name of table collection
     * @param nameResource   - name of document instance
     * @throws SomethingWrongWithDataBaseException - if there are some problems with connect to Data Base
     */
    void delete(String nameCollection, String nameResource);

    /**
     * Updates value of Document instance for the specified name of document
     * from the specified table in Data base
     *
     * @param nameCollection - name of table collection
     * @param nameResource   - name of document instance
     * @param document       - undated Document instance
     * @throws SomethingWrongWithDataBaseException - if there are some problems with connect to Data Base
     */
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

    boolean consist(String nameCollection, String nameResource);
}
