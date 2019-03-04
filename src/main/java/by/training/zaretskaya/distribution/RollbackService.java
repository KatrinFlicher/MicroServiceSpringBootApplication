package by.training.zaretskaya.distribution;

import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.FailedOperationException;
import by.training.zaretskaya.models.Collection;
import by.training.zaretskaya.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class RollbackService {
    @Autowired
    private DistributedService2 service;

    public RollbackService() {
    }

    public void rollback(int counter, String... parameters) {
        if (counter == 0) {
            throw new FailedOperationException();
        }
        if (parameters.length == 2) {
            service.sendDeleteObject(counter, Constants.ROLLBACK_ON, parameters[0], parameters[1]);
        } else {
            service.sendDeleteObject(counter, Constants.ROLLBACK_ON, parameters[0]);
        }
    }

    public void rollback(Object object, int counter, HttpMethod method, String... parameters) {
        Collection collection = null;
        Document document = null;
        if (counter == 0) {
            throw new FailedOperationException();
        }
        if (object instanceof Collection) {
            collection = (Collection) object;
        } else {
            document = (Document) object;
        }
        switch (method) {
            case DELETE:
                if (parameters.length == 2) {
                    service.sendPostObject(document, counter, Constants.ROLLBACK_ON, parameters[0], parameters[1]);
                } else {
                    service.sendPostObject(collection, counter, Constants.ROLLBACK_ON, parameters[0]);
                }
                break;
            case PUT:
                if (parameters.length == 2) {
                    service.sendUpdateObject(document, counter, Constants.ROLLBACK_ON, parameters[0], parameters[1]);
                } else {
                    service.sendUpdateObject(collection, counter, Constants.ROLLBACK_ON, parameters[0]);
                }
                break;
        }
    }
}
