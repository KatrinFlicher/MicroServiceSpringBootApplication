package by.training.zaretskaya.distribution;

import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.FailedOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class RollbackService {
    @Autowired
    private DistributedService service;

    public RollbackService() {
    }

    public void rollback(int counter, String... parameters) {
        if (counter == 0) {
            throw new FailedOperationException();
        }
        if (parameters.length == 2) {
            service.sendDeleteObject(counter, Constants.ROLLBACK_ON,
                    parameters[Constants.POSITION_ID_COLLECTION], parameters[Constants.POSITION_ID_DOCUMENT]);
        } else {
            service.sendDeleteObject(counter, Constants.ROLLBACK_ON, parameters[Constants.POSITION_ID_COLLECTION]);
        }
    }

    public void rollback(Object object, int counter, HttpMethod method, String... parameters) {
        if (counter == 0) {
            throw new FailedOperationException();
        }
        switch (method) {
            case DELETE:
                service.sendPostObject(object, counter, Constants.ROLLBACK_ON,
                        parameters[Constants.POSITION_ID_COLLECTION]);
                break;
            case PUT:
                if (parameters.length == 2) {
                    service.sendUpdateObject(object, counter, Constants.ROLLBACK_ON,
                            parameters[Constants.POSITION_ID_COLLECTION], parameters[Constants.POSITION_ID_DOCUMENT]);
                } else {
                    service.sendUpdateObject(object, counter, Constants.ROLLBACK_ON,
                            parameters[Constants.POSITION_ID_COLLECTION]);
                }
                break;
        }
    }
}
