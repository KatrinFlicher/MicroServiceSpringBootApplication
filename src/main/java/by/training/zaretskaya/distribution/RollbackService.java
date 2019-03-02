package by.training.zaretskaya.distribution;

import by.training.zaretskaya.constants.Constants;
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

    public void rollback(String counter, HttpMethod method, String... parameters) {
        switch (method) {
            case POST:
                if (object instanceof Collection){
                    Collection collection = (Collection) object;
                    service.sendPostObject(collection, counter, Constants.ROLLBACK_ON, collection.getName());
                }
                else {
                    Document document = (Document) object;
                    service.sendPostObject(document, counter,
                            Constants.ROLLBACK_ON, parameters[0], document.getKey());
                }
                break;
            case DELETE:
                if (parameters.length == 2){
                    service.sendDeleteObject(counter, Constants.ROLLBACK_ON, parameters[0], parameters[1]);
                }
                else {
                    service.sendDeleteObject(counter, Constants.ROLLBACK_ON, parameters[0]);
                }
                break;
            case PUT:
                service.sendUpdateObject(object, counter, Constants.ROLLBACK_ON, parameters[0], parameters[1]);
                break;
        }
    }
}
