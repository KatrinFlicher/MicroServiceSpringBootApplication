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
    private Object object;

    public RollbackService() {
    }

    public void saveResource(Object object) {
        this.object = object;
    }

    public Object getResource() {
        return this.object;
    }


    public void rollback(int counter, HttpMethod method, String... parameters) {
        if (counter == 0) {
            throw new FailedOperationException();
        }
        switch (method) {
            case POST:
                System.out.println(object);
                System.out.println("in rollback");
                if (parameters.length == 2) {
                    service.sendDeleteObject(counter, Constants.ROLLBACK_ON, parameters[0], parameters[1]);
                } else {
                    System.out.println("before sebnd" + counter + parameters[0]);
                    service.sendDeleteObject(counter, Constants.ROLLBACK_ON, parameters[0]);
                }
                break;
            case DELETE:
                if (object instanceof Collection) {
                    System.out.println(object);
                    System.out.println("in rollback");
                    Collection collection = (Collection) object;
                    service.sendPostObject(collection, counter, Constants.ROLLBACK_ON, collection.getName());
                } else {
                    Document document = (Document) object;
                    service.sendPostObject(document, counter,
                            Constants.ROLLBACK_ON, parameters[0], document.getKey());
                }
                break;
            case PUT:
                service.sendUpdateObject(object, counter, Constants.ROLLBACK_ON, parameters[0], parameters[1]);
                break;
        }
    }
}
