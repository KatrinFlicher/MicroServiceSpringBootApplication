package by.training.zaretskaya.distribution;

import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.FailedOperationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Component
public class RollbackService {
    private static final Logger log = LogManager.getLogger(RollbackService.class);
    @Autowired
    private DistributedService service;

    public RollbackService() {
    }

    public void rollback(int counter, String... parameters) {
        if (counter == 0) {
            log.info("There are not changed nodes for rollback");
            throw new FailedOperationException();
        }
        log.warn("Starting rollback for POST request...");
        if (parameters.length == 2) {
            service.sendDeleteObject(counter, Constants.ROLLBACK_ON,
                    parameters[Constants.POSITION_ID_COLLECTION], parameters[Constants.POSITION_ID_DOCUMENT]);
        } else {
            service.sendDeleteObject(counter, Constants.ROLLBACK_ON, parameters[Constants.POSITION_ID_COLLECTION]);
        }
        log.info("Rollback for CREATE request is successfully executed");
        throw new FailedOperationException();
    }

    public void rollback(Object object, int counter, HttpMethod method, String... parameters) {
        if (counter == 0) {
            log.info("There are not changed nodes for rollback");
            throw new FailedOperationException();
        }
        switch (method) {
            case DELETE:
                log.warn("Starting rollback for DELETE request...");
                service.sendPostObject(object, counter, Constants.ROLLBACK_ON,
                        parameters[Constants.POSITION_ID_COLLECTION]);
                break;
            case PUT:
                log.warn("Starting rollback for PUT request...");
                if (parameters.length == 2) {
                    service.sendUpdateObject(object, counter, Constants.ROLLBACK_ON,
                            parameters[Constants.POSITION_ID_COLLECTION], parameters[Constants.POSITION_ID_DOCUMENT]);
                } else {
                    service.sendUpdateObject(object, counter, Constants.ROLLBACK_ON,
                            parameters[Constants.POSITION_ID_COLLECTION]);
                }
                break;
        }
        log.info("Rollback for " + (method == HttpMethod.PUT ? "PUT" : "DELETE") + " request is successfully executed");
        throw new FailedOperationException();
    }
}
