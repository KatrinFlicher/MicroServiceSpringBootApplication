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
        documentDAO.create(nameCollection, document);
    }

    @Override
    public Document get(String nameCollection, String nameResource) {
        ICache<String, Document> cache = collectionDAO
                .getById(nameCollection)
                .getCache();
        if (!cache.contains(nameResource)) {
            Document document = documentDAO.get(nameCollection, nameResource);
            cache.put(nameResource, document);
        }
        return cache.get(nameResource);
    }

    @Override
    public void delete(String nameCollection, String nameResource) {
        documentDAO.delete(nameCollection, nameResource);
        Collection collection = collectionDAO.getById(nameCollection);
        collection.getCache().remove(nameResource);
    }

    @Override
    public void update(String nameCollection, String nameResource, Document document) {
        documentDAO.update(nameCollection, nameResource, document);
        ICache<String, Document> cache = collectionDAO.getById(nameCollection).getCache();
        if (cache.contains(nameResource)) {
            String newValue = document.getValue();
            cache.get(nameResource).setKey(newValue);
        }
    }

    @Override
    public List<Document> list(String nameCollection, String objectToCompare, int size) {
        return documentDAO.list(nameCollection, objectToCompare, size);
    }
}
