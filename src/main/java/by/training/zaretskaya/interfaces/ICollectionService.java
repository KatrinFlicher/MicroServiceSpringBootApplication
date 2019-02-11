package by.training.zaretskaya.interfaces;


import java.util.List;

public interface ICollectionService<C> {
    void create(C collection);

    C getById(String name);

    void delete(String name);

    void updateName(String name, String newName);

    void updateCacheLimit(String name, int cacheLimit);

    void updateAlgorithm(String name, String algorithm);

    List<C> listCollections(int page, int size);
}
