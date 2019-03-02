package by.training.zaretskaya.validators;

import by.training.zaretskaya.interfaces.CollectionDAO;
import by.training.zaretskaya.interfaces.DocumentDAO;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.models.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class EntityValidatorTest {
    @Mock
    CollectionDAO<Collection> collectionDAO;

    @Mock
    DocumentDAO<Document> documentDAO;

    @InjectMocks
    EntityValidator entityValidator;


    @Test
    public void checkNewNameForTable() {
    }

    @Test
    public void checkValidationCacheLimit() {
    }

    @Test
    public void checkValidationAlgorithm() {
    }

    @Test
    public void checkExistenceOfCollection() {
    }

    @Test
    public void checkExistenceOfCollectionAndDocument() {
    }

    @Test
    public void checkAbsenceOfNewDocumentInTheTable() {
    }

    @Test
    public void validationDocumentUnderTheScheme() {
    }
}