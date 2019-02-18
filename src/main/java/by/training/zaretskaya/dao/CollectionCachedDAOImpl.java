package by.training.zaretskaya.dao;

import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.impl.FactoryCache;
import by.training.zaretskaya.impl.LFUCacheImpl;
import by.training.zaretskaya.interfaces.CollectionDAO;
import by.training.zaretskaya.interfaces.ICache;
import by.training.zaretskaya.models.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Qualifier("CollectionCachedDAO")
public class CollectionCachedDAOImpl implements CollectionDAO<Collection> {

    @Autowired
    @Qualifier("CollectionDAO")
    CollectionDAO<Collection> collectionDao;
    private ICache<String, Collection> mapCollection = new LFUCacheImpl(Constants.MAX_SIZE_FOR_CACHE_COLLECTIONS);

    @PostConstruct
    void fillMap() {
        int startPage = 1;
        List<Collection> list = collectionDao.list(startPage, Constants.MAX_SIZE_FOR_CACHE_COLLECTIONS);
        list.stream().forEach((collection) -> {
            collection.setCache(FactoryCache
                    .createCache(collection.getAlgorithm(), collection.getCacheLimit()));
            mapCollection.put(collection.getName(), collection);
        });
    }

    @Override
    public void create(Collection collection) {
        collectionDao.create(collection);
    }

    @Override
    public Collection getById(String name) {
        if (!mapCollection.contains(name)) {
            Collection collection = collectionDao.getById(name);
            mapCollection.put(collection.getName(), collection);
        }
        return mapCollection.get(name);
    }

    @Override
    public void delete(String name) {
        collectionDao.delete(name);
        mapCollection.remove(name);
    }

    @Override
    public void updateName(String name, String newName) {
        collectionDao.updateName(name, newName);
        if (mapCollection.contains(name)) {
            Collection collection = mapCollection.remove(name);
            collection.setName(newName);
            mapCollection.put(collection.getName(), collection);
        }
    }

    @Override
    public void updateCacheLimit(String name, int cacheLimit) {
        collectionDao.updateCacheLimit(name, cacheLimit);
        if (mapCollection.contains(name)) {
            Collection collection = mapCollection.get(name);
            collection.setCacheLimit(cacheLimit);
            collection.setCache(FactoryCache.createCache(collection.getAlgorithm(), collection.getCacheLimit()));
        }
    }

    @Override
    public void updateAlgorithm(String name, String algorithm) {
        collectionDao.updateAlgorithm(name, algorithm);
        if (mapCollection.contains(name)) {
            Collection collection = mapCollection.get(name);
            collection.setAlgorithm(algorithm);
            collection.setCache(FactoryCache.createCache(collection.getAlgorithm(), collection.getCacheLimit()));
        }
    }

    @Override
    public List<Collection> list(int page, int size) {
        return collectionDao.list(page, size);
    }
}
