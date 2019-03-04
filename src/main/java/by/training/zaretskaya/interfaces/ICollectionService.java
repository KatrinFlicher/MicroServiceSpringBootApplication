package by.training.zaretskaya.interfaces;


import java.util.List;

public interface ICollectionService<C> {
    void create(C collection);

    C getById(String name);

    void delete(String name);

    void update(String name, C collection);

    List<C> listCollections(int page, int size);
}
