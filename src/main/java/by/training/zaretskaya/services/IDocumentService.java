package by.training.zaretskaya.services;

import java.util.List;

public interface IDocumentService<D> {
    void create(String nameCollection, D document);

    D get(String nameCollection, String nameResource);

    void delete(String nameCollection, String nameResource);

    void update(String nameCollection, String nameResource, D document);

    List<D> list(String nameCollection, String objectToCompare, int size);
}