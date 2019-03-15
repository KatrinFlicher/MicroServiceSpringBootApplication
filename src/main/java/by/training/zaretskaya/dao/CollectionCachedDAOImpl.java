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
        List<Collection> list = collectionDao.list(Constants.DEFAULT_OBJECT_TO_COMPARE,
                Constants.MAX_SIZE_FOR_CACHE_COLLECTIONS);
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
    public void update(String name, Collection collection) {
        collectionDao.update(name, collection);
        if (mapCollection.contains(name)) {
            Collection collectionFromDB = mapCollection.get(name);
            collectionFromDB.setCacheLimit(collection.getCacheLimit());
            collection.setAlgorithm(collection.getAlgorithm());
            collectionFromDB.setCache(FactoryCache.createCache(collection.getAlgorithm(), collection.getCacheLimit()));
        }
    }

    @Override
    public List<Collection> list(String objectToCompare, int size) {
        return collectionDao.list(objectToCompare, size);
    }

    @Override
    public boolean consist(String name) {
        if (!mapCollection.contains(name)){
            return collectionDao.consist(name);
        }
        return true;
    }
}
