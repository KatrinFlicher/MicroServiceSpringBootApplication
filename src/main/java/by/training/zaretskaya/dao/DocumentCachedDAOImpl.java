package by.training.zaretskaya.dao;

import by.training.zaretskaya.interfaces.CollectionDAO;
import by.training.zaretskaya.interfaces.DocumentDAO;
import by.training.zaretskaya.interfaces.ICache;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("DocumentCachedDAO")
public class DocumentCachedDAOImpl implements DocumentDAO<Document> {

    @Autowired
    @Qualifier("DocumentDao")
    DocumentDAO<Document> documentDAO;

    @Autowired
    @Qualifier("CollectionCachedDAO")
    CollectionDAO<Collection> collectionDAO;

    @Override
    public void create(String nameCollection, Document document) {
        Collection collection = collectionDAO.getById(nameCollection);
        documentDAO.create(nameCollection, document);
        collection.getCache()
                .put(document.getKey(), document);
    }

    @Override
    public Document get(String nameCollection, String nameResource) {
        ICache<String, Document> cache = collectionDAO
                .getById(nameCollection)
                .getCache();
        if (!cache.contains(nameResource)) {
//            System.out.println("get from db");
            Document document = (Document) documentDAO.get(nameCollection, nameResource);
            cache.put(document.getKey(), document);
        }
        return cache.get(nameResource);
    }

    @Override
    public void delete(String nameCollection, String nameResource) {
        Collection collection = collectionDAO.getById(nameCollection);
        documentDAO.delete(nameCollection, nameResource);
        collection.getCache()
                .remove(nameResource);
    }

    @Override
    public void update(String nameCollection, String nameResource, Document document) {
        Collection collection = collectionDAO.getById(nameCollection);
        documentDAO.update(nameCollection, nameResource, document);
        Document oldDocument = collection.getCache()
                .get(nameResource);
        oldDocument.setValue(document.getValue());
    }

    @Override
    public List list(String nameCollection, int page, int size) {
        return documentDAO.list(nameCollection, page, size);
    }

}
