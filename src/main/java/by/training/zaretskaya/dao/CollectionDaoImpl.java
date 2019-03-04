package by.training.zaretskaya.dao;


import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.constants.SQLConstants;
import by.training.zaretskaya.exception.CollectionNameNotSupportedException;
import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.exception.SomethingWrongWithDataBaseException;
import by.training.zaretskaya.interfaces.CollectionDAO;
import by.training.zaretskaya.models.Collection;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.sql.SQLException;
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
        try {
            checkNameTable(collection.getName());
            entityManager.persist(collection);
            String sqlQuery = SQLConstants.CREATE_NAMED_TABLE_FOR_DOCUMENTS
                    .replace(SQLConstants.MOCK_NAME_COLLECTION, collection.getName());
            entityManager.createNativeQuery(sqlQuery).executeUpdate();
//            entityManager.flush();
        } catch (Exception e) {
            throw new SomethingWrongWithDataBaseException();
        }
    }

    @Override
    public Collection getById(String name) {
        Collection collection = null;
        try {
            collection = entityManager.find(Collection.class, name);
        } catch (Exception e) {
            throw new SomethingWrongWithDataBaseException();
        }
        if (collection == null) {
            throw new ResourceNotFoundException(Constants.RESOURCE_COLLECTION, name);
        }
        return collection;
    }

    @Override
    public void delete(String name) {
        try {
            checkNameTable(name);
            Collection collection = getById(name);
            entityManager.remove(collection);
//            entityManager.flush();
            String sqlQuery = SQLConstants.DROP_NAMED_DOCUMENT_TABLE
                    .replace(SQLConstants.MOCK_NAME_COLLECTION, name);
            entityManager.createNativeQuery(sqlQuery).executeUpdate();
//            entityManager.flush();
        } catch (Exception e) {
            throw new SomethingWrongWithDataBaseException();
        }
    }

    @Override
    public void update(String name, Collection collection) {
        try{
            Collection collectionFromDB = getById(name);
            collectionFromDB.setCacheLimit(collection.getCacheLimit());
            collectionFromDB.setAlgorithm(collection.getAlgorithm());
            entityManager.persist(collectionFromDB);
        }
        catch (Exception e){
            throw new SomethingWrongWithDataBaseException();
        }
    }

    @SuppressWarnings("JpaQueryApiInspection")
    @Override
    public List<Collection> list(int page, int size) {
        try {
            TypedQuery<Collection> query = entityManager
                    .createNamedQuery("Collection.findAllCollections", Collection.class);
            query.setFirstResult((page - 1) * size);
            query.setMaxResults(size);
            return query.getResultList();
        } catch (Exception e) {
            throw new SomethingWrongWithDataBaseException();
        }
    }
}
