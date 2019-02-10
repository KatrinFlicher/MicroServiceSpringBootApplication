package by.training.zaretskaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DocumentIsInvalidUnderTheScheme extends RuntimeException {
    private static String message = "Document is not supported to the json scheme.";

    public DocumentIsInvalidUnderTheScheme() {
        super(message);
    }
}
