package by.training.zaretskaya.dao;

import by.training.zaretskaya.cache.ICache;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Qualifier("DocumentCachedDAO")
public class DocumentCachedDAOImpl implements DocumentDAO<Document> {

    private CollectionDAO<Collection> collectionDAO;
    private DocumentDAO<Document> documentDAO;

    @Autowired
    public DocumentCachedDAOImpl(@Qualifier("CollectionCachedDAO") CollectionDAO<Collection> collectionDAO,
                                 @Qualifier("DocumentDao") DocumentDAO<Document> documentDAO) {
        this.documentDAO = documentDAO;
        this.collectionDAO = collectionDAO;
    }

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

    @Override
    public boolean contains(String nameCollection, String nameResource) {
        ICache<String, Document> cache = collectionDAO
                .getById(nameCollection)
                .getCache();
        if (!cache.contains(nameResource)) {
            return documentDAO.contains(nameCollection, nameResource);
        }
        return true;
    }
}
