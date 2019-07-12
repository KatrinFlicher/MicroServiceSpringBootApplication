package by.training.zaretskaya.services;


import java.util.List;
/**
 * Interface with CRUD operations and list with the entity Document in Service layer
 * for additional manipulation like validation ect.
 *
 * @author Zaretskaya Katsiaryna
 * @version 1.0
 */
public interface ICollectionService<C> {
    void create(C collection);

    C getById(String name);

    void delete(String name);

    void update(String name, C collection);

    List<C> list(String objectToCompare, int size);
}
