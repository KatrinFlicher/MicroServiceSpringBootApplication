package by.training.zaretskaya.distribution;

import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.models.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class RollbackService {
    @Autowired
    private DistributedService2 service;
    private Collection collection;

    public RollbackService() {
    }

    public void saveResource(Collection collection) {
        this.collection = collection;
    }

    public void rollback(String counter, HttpMethod method, String... fieldForPutMethod) {
        switch (method) {
            case POST:
                service.sendDelete(collection.getName(), counter, Constants.ROLLBACK_ON);
                break;
            case DELETE:
                service.sendPost(collection, counter, Constants.ROLLBACK_ON);
                break;
            case PUT:
                service.sendUpdate(collection.getName(), counter, collection,
                        fieldForPutMethod[0], Constants.ROLLBACK_ON);
                break;
        }
    }
}
