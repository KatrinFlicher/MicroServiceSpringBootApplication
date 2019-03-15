package by.training.zaretskaya.exception;

import by.training.zaretskaya.constants.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CollectionNameNotSupportedException extends RuntimeException {

    public CollectionNameNotSupportedException() {
        super(Constants.COLLECTION_NAME_NOT_SUPPORTED);
    }
}
