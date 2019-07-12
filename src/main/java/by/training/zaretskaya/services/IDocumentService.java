package by.training.zaretskaya.services;

import java.util.List;

/**
 * Interface with CRUD operations and list with the entity Collection in Service layer
 * for additional manipulation like validation ect.
 *
 * @author Zaretskaya Katsiaryna
 * @version 1.0
 */
public interface IDocumentService<D> {
    void create(String nameCollection, D document);

    D get(String nameCollection, String nameResource);

    void delete(String nameCollection, String nameResource);

    void update(String nameCollection, String nameResource, D document);

    List<D> list(String nameCollection, String objectToCompare, int size);
}
