package by.training.zaretskaya.dao;

import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.constants.SQLConstants;
import by.training.zaretskaya.exception.ResourceNotFoundException;
import by.training.zaretskaya.interfaces.DocumentDAO;
import by.training.zaretskaya.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
        String sqlQuery = prepareSqlQuery(nameCollection, SQLConstants.INSERT_DOCUMENT_TO_TABLE);
        jdbcTemplate.update(sqlQuery, document.getKey(), document.getValue());
    }

    @Override
    public Document get(String nameCollection, String nameResource) {
        String sqlQuery = prepareSqlQuery(nameCollection, SQLConstants.SELECT_DOCUMENT_BY_KEY);
        try {
            return jdbcTemplate.queryForObject(sqlQuery,
                    new Object[]{nameResource},
                    (resultSet, i) -> new Document(resultSet.getString(SQLConstants.DOCUMENT_KEY),
                            resultSet.getString(SQLConstants.DOCUMENT_VALUE)));
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(Constants.RESOURCE_DOCUMENT, nameResource);
        }
    }

    @Override
    public void delete(String nameCollection, String nameResource) {
        String sqlQuery = prepareSqlQuery(nameCollection, SQLConstants.DELETE_DOCUMENT_BY_KEY);
        jdbcTemplate.update(sqlQuery, nameResource);
    }

    @Override
    public void update(String nameCollection, String nameResource, Document document) {
        String sqlQuery = prepareSqlQuery(nameCollection, SQLConstants.UPDATE_DOCUMENT_BY_KEY);
        jdbcTemplate.update(sqlQuery, document.getValue(), nameResource);
    }

    @Override
    public List<Object> list(String nameCollection, int page, int size) {
        String sqlQuery = prepareSqlQuery(nameCollection, SQLConstants.SELECT_ALL_DOCUMENTS_FROM_TABLE);
        return jdbcTemplate.query(sqlQuery, new Object[]{size, (page - 1) * size},
                new BeanPropertyRowMapper(Document.class));
    }

    private String prepareSqlQuery(String nameTable, String query) {
        String preparedQuery = query.replace(SQLConstants.MOCK_NAME_COLLECTION, nameTable);
        System.out.println(preparedQuery);
        return preparedQuery;
    }
}
