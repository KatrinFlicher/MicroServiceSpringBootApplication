package by.training.zaretskaya.interfaces;


import by.training.zaretskaya.models.Collection;

import java.util.List;


public interface CollectionDAO {
    void create(Collection collection);

    Collection getById(String name);

    void delete(String name);

    void updateName(String name, String newName);

    void updateCacheLimit(String name, int cacheLimit);

    void updateAlgorithm(String name, String algorithm);

    List<Collection> list(int page, int size);
}
