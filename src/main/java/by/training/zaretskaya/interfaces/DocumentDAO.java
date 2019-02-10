package by.training.zaretskaya.interfaces;


import by.training.zaretskaya.models.Document;

import java.util.List;

public interface DocumentDAO {
    void create(String nameCollection, Document document);

    Document get(String nameCollection, String nameResource);

    void delete(String nameCollection, String nameResource);

    void update(String nameCollection, String nameResource, Document document);

    List list(String nameCollection, int page, int size);
}
