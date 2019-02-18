package by.training.zaretskaya.services;


import by.training.zaretskaya.interfaces.CollectionDAO;
import by.training.zaretskaya.interfaces.ICollectionService;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.validators.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionServiceImpl implements ICollectionService<Collection> {

    @Autowired
    @Qualifier("CollectionCachedDAO")
    CollectionDAO<Collection> collectionDAO;

    @Autowired
    EntityValidator validator;

    @Override
    public void create(Collection collection) {
        validator.checkNewNameForTable(collection.getName());
        validator.checkValidationCacheLimit(collection.getCacheLimit());
        validator.checkValidationAlgorithm(collection.getAlgorithm());
        collectionDAO.create(collection);
    }

    @Override
    public Collection getById(String name) {
        return collectionDAO.getById(name);
    }

    @Override
    public void delete(String name) {
        collectionDAO.delete(name);
    }

    @Override
    public void updateName(String name, String newName) {
        validator.checkNewNameForTable(newName);
        collectionDAO.updateName(name, newName);
    }

    @Override
    public void updateCacheLimit(String name, int cacheLimit) {
        validator.checkValidationCacheLimit(cacheLimit);
        collectionDAO.updateCacheLimit(name, cacheLimit);
    }

    @Override
    public void updateAlgorithm(String name, String algorithm) {
        validator.checkValidationAlgorithm(algorithm);
        collectionDAO.updateAlgorithm(name, algorithm);
    }

    @Override
    public List<Collection> listCollections(int page, int size) {
        return collectionDAO.list(page, size);
    }

}
