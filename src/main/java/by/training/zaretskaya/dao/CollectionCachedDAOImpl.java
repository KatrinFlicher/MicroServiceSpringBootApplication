package by.training.zaretskaya.dao;

import by.training.zaretskaya.impl.FactoryCache;
import by.training.zaretskaya.interfaces.CollectionDAO;
import by.training.zaretskaya.models.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Qualifier("CollectionCachedDAO")
public class CollectionCachedDAOImpl implements CollectionDAO<Collection> {

    Map<String, Collection> mapCollection = new HashMap<>();

    @Autowired
    @Qualifier("CollectionDAO")
    CollectionDAO<Collection> collectionDao;

    @PostConstruct
    private void fillMap() {
        List<Collection> list = collectionDao.list(1, 50);
        list.stream().forEach((collection) -> {
            try {
                collection.setCache(FactoryCache
                        .createCache(collection.getAlgorithm(), collection.getCacheLimit()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            mapCollection.put(collection.getName(), collection);
        });
    }

    @Override
    public void create(Collection collection) {
        collectionDao.create(collection);
        collection.setCache(FactoryCache.createCache(collection.getAlgorithm(), collection.getCacheLimit()));
        mapCollection.put(collection.getName(), collection);
    }

    @Override
    public Collection getById(String name) {
        if (!mapCollection.containsKey(name)) {
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
        Collection collection = mapCollection.remove(name);
        collection.setName(newName);
        mapCollection.put(collection.getName(), collection);
    }

    @Override
    public void updateCacheLimit(String name, int cacheLimit) {
        collectionDao.updateCacheLimit(name, cacheLimit);
        Collection collection = mapCollection.get(name);
        collection.setCacheLimit(cacheLimit);
        collection.setCache(FactoryCache.createCache(collection.getAlgorithm(), collection.getCacheLimit()));
    }

    @Override
    public void updateAlgorithm(String name, String algorithm) {
        collectionDao.updateAlgorithm(name, algorithm);
        Collection collection = mapCollection.get(name);
        collection.setAlgorithm(algorithm);
        collection.setCache(FactoryCache.createCache(collection.getAlgorithm(), collection.getCacheLimit()));
    }

    @Override
    public List<Collection> list(int page, int size) {
        return collectionDao.list(page, size);
    }


}
