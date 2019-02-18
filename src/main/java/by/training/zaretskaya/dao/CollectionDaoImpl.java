package by.training.zaretskaya.dao;


import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.constants.SQLConstants;
import by.training.zaretskaya.exception.CollectionNameNotSupportedException;
import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.interfaces.CollectionDAO;
import by.training.zaretskaya.models.Collection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Qualifier("CollectionDAO")
@Transactional
public class CollectionDaoImpl implements CollectionDAO<Collection> {

    @PersistenceUnit
    private EntityManagerFactory managerFactory;

    @PersistenceContext
    private EntityManager entityManager;

    private void checkNameTable(String nameTable) {
        Pattern p = Pattern.compile(Constants.PATTERN_FOR_NAME_COLLECTION);
        Matcher m = p.matcher(nameTable);
        if (!m.matches()) {
            throw new CollectionNameNotSupportedException();
        }
    }

    @Override
    public void create(Collection collection) {
        checkNameTable(collection.getName());
        entityManager.persist(collection);
        String sqlQuery = SQLConstants.CREATE_NAMED_TABLE_FOR_DOCUMENTS
                .replace(SQLConstants.MOCK_NAME_COLLECTION, collection.getName());
        entityManager.createNativeQuery(sqlQuery).executeUpdate();
        entityManager.flush();
    }

    @Override
    public Collection getById(String name) {
        Collection collection = entityManager.find(Collection.class, name);
        if (collection == null) {
            throw new ResourceNotFoundException(Constants.RESOURCE_COLLECTION, name);
        }
        return collection;
    }


    @Override
    public void delete(String name) {
        checkNameTable(name);
        Collection collection = getById(name);
        entityManager.remove(collection);
        entityManager.flush();
        String sqlQuery = SQLConstants.DROP_NAMED_DOCUMENT_TABLE
                .replace(SQLConstants.MOCK_NAME_COLLECTION, name);
        entityManager.createNativeQuery(sqlQuery).executeUpdate();
        entityManager.flush();
    }

    @Override
    public void updateName(String name, String newName) {
        checkNameTable(name);
        checkNameTable(newName);
        Collection collection = getById(name);
        entityManager.remove(collection);
        entityManager.flush();
        collection.setName(newName);
        entityManager.persist(collection);
        String sqlQuery = SQLConstants.ALTER_NAMED_DOCUMENT_TABLE
                .replace(SQLConstants.MOCK_NAME_COLLECTION, name)
                .replace(SQLConstants.MOCK_NEW_NAME_COLLECTION, newName);
        entityManager.createNativeQuery(sqlQuery).executeUpdate();
        entityManager.flush();
    }

    @Override
    public void updateCacheLimit(String name, int cacheLimit) {
        Collection collection = getById(name);
        collection.setCacheLimit(cacheLimit);
        entityManager.refresh(collection);
        entityManager.flush();
    }

    @Override
    public void updateAlgorithm(String name, String algorithm) {
        Collection collection = getById(name);
        collection.setAlgorithm(algorithm);
        entityManager.refresh(collection);
        entityManager.flush();
    }


    @SuppressWarnings("JpaQueryApiInspection")
    @Override
    public List<Collection> list(int page, int size) {
        TypedQuery<Collection> query = entityManager
                .createNamedQuery("Collection.findAllCollections", Collection.class);
        query.setFirstResult((page - 1) * size);
        query.setMaxResults(size);
        return query.getResultList();
    }
}
