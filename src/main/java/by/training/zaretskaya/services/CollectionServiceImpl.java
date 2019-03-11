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
        validator.checkValidationCollection(collection);
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
    public void update(String name, Collection collection) {
        validator.checkValidationCollection(collection);
        collectionDAO.update(name, collection);
    }

    @Override
    public List<Collection> listCollections(String objectToCompare, int size) {
        return collectionDAO.list(objectToCompare, size);
    }

}
