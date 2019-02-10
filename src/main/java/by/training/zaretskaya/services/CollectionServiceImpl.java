package by.training.zaretskaya.services;


import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.CollectionWrongParameters;
import by.training.zaretskaya.exception.ResourceIsExistException;
import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.impl.FactoryCache;
import by.training.zaretskaya.interfaces.CollectionDAO;
import by.training.zaretskaya.interfaces.ICollectionService;
import by.training.zaretskaya.models.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionServiceImpl implements ICollectionService {

    @Autowired
    @Qualifier("CollectionCachedDAO")
    CollectionDAO collectionDAO;

    @Override
    public void create(Collection collection) {
        try {
            getById(collection.getName());
            throw new ResourceIsExistException(Constants.RESOURCE_COLLECTION, collection.getName());
        } catch (ResourceNotFoundException e) {
            checkValidationCacheLimit(collection.getCacheLimit());
            checkValidationAlgorithm(collection.getAlgorithm());
            collectionDAO.create(collection);
        }
    }

    @Override
    public Collection getById(String name) {
        return collectionDAO.getById(name);
    }

    @Override
    public void delete(String name) {
        checkCollection(name);
        collectionDAO.delete(name);
    }

    @Override
    public void updateName(String name, String newName) {
        checkCollection(name);
        collectionDAO.updateName(name, newName);
    }

    @Override
    public void updateCacheLimit(String name, int cacheLimit) {
        checkCollection(name);
        checkValidationCacheLimit(cacheLimit);
        collectionDAO.updateCacheLimit(name, cacheLimit);
    }

    @Override
    public void updateAlgorithm(String name, String algorithm) {
        checkCollection(name);
        checkValidationAlgorithm(algorithm);
        collectionDAO.updateAlgorithm(name, algorithm);
    }

    @Override
    public List<Collection> listCollections(int page, int size) {
        return collectionDAO.list(page, size);
    }

    private void checkCollection(String nameCollection) {
        getById(nameCollection);
    }

    private void checkValidationAlgorithm(String algorithm) {
        try {
            FactoryCache.TypeCache.valueOf(algorithm);
        } catch (IllegalArgumentException e) {
            throw new CollectionWrongParameters(Constants.INCOMPATIBLE_FORMAT_CACHE_ALGORITHM, algorithm);
        }
    }

    private void checkValidationCacheLimit(int cacheLimit) {
        if (cacheLimit < 0) {
            throw new CollectionWrongParameters(Constants.NEGATIVE_CACHE_LIMIT, String.valueOf(cacheLimit));
        }
    }
}
