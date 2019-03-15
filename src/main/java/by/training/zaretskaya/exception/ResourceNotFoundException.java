package by.training.zaretskaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private static String message = "$resource with id $id is not found.";
    private static String identifierResource = "$resource";
    private static String identifierId = "$id";

    public ResourceNotFoundException(String nameResource, String id) {
        super(message
                .replace(identifierResource, nameResource)
                .replace(identifierId, id));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
