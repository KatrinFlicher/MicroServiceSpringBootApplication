package by.training.zaretskaya.dao;

import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.constants.SQLConstants;
import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.exception.SomethingWrongWithDataBaseException;
import by.training.zaretskaya.interfaces.DocumentDAO;
import by.training.zaretskaya.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;


@Component
@Qualifier("DocumentDao")
@Transactional
public class DocumentDaoImpl implements DocumentDAO<Document> {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void create(String nameCollection, Document document) {
        try {
            String sqlQuery = prepareSqlQuery(nameCollection, SQLConstants.INSERT_DOCUMENT_TO_TABLE);
            jdbcTemplate.update(sqlQuery, document.getKey(), document.getValue());
        } catch (DataAccessException e) {
            throw new SomethingWrongWithDataBaseException(e);
        }
    }

    @Override
    public Document get(String nameCollection, String nameResource) {
        try {
            String sqlQuery = prepareSqlQuery(nameCollection, SQLConstants.SELECT_DOCUMENT_BY_KEY);
            return jdbcTemplate.queryForObject(sqlQuery,
                    new Object[]{nameResource},
                    (resultSet, i) -> new Document(resultSet.getString(SQLConstants.DOCUMENT_KEY),
                            resultSet.getString(SQLConstants.DOCUMENT_VALUE)));
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(Constants.RESOURCE_DOCUMENT, nameResource);
        } catch (DataAccessException e) {
            throw new SomethingWrongWithDataBaseException(e);
        }
    }

    @Override
    public void delete(String nameCollection, String nameResource) {
        try {
            String sqlQuery = prepareSqlQuery(nameCollection, SQLConstants.DELETE_DOCUMENT_BY_KEY);
            jdbcTemplate.update(sqlQuery, nameResource);
        } catch (DataAccessException e) {
            throw new SomethingWrongWithDataBaseException(e);
        }
    }

    @Override
    public void update(String nameCollection, String nameResource, Document document) {
        try {
            String sqlQuery = prepareSqlQuery(nameCollection, SQLConstants.UPDATE_DOCUMENT_BY_KEY);
            jdbcTemplate.update(sqlQuery, document.getValue(), nameResource);
        } catch (DataAccessException e) {
            throw new SomethingWrongWithDataBaseException(e);
        }
    }

    @Override
    public List<Document> list(String nameCollection, String objectToCompare, int size) {
        try {
            String sqlQuery = prepareSqlQuery(nameCollection, SQLConstants.SELECT_ALL_DOCUMENTS_FROM_TABLE);
            return jdbcTemplate.query(sqlQuery, new Object[]{objectToCompare, size},
                    new BeanPropertyRowMapper(Document.class));
        } catch (DataAccessException e) {
            throw new SomethingWrongWithDataBaseException(e);
        }

    }

    private String prepareSqlQuery(String nameTable, String query) {
        return query.replace(SQLConstants.MOCK_NAME_COLLECTION, nameTable);
    }
}
