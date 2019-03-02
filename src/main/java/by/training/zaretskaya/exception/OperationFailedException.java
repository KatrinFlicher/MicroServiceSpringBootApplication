package by.training.zaretskaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class OperationFailedException extends RuntimeException {
    private static String message = "Operation failed. Something wrong with data base.";

    public OperationFailedException() {
        super(message);
    }

}
