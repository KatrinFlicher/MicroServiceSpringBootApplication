package by.training.zaretskaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CollectionNameNotSupportedException extends RuntimeException {
    private static String message = "This name for collection is not supported.";

    public CollectionNameNotSupportedException() {
        super(message);
    }
}
