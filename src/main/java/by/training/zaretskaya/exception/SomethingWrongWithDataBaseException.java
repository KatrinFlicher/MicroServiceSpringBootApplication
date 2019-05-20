package by.training.zaretskaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class SomethingWrongWithDataBaseException extends RuntimeException {

    public SomethingWrongWithDataBaseException() {
        super();
    }

    public SomethingWrongWithDataBaseException(Throwable cause) {
        super(cause);
    }
}
