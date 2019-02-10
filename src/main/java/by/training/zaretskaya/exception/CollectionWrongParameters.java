package by.training.zaretskaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CollectionWrongParameters extends RuntimeException {
    private static String identifierWrongValue = "$value";

    public CollectionWrongParameters(String message, String wrongValue) {
        super(message.replace(identifierWrongValue, wrongValue));
    }

    public CollectionWrongParameters(String message) {
        super(message);
    }
}
