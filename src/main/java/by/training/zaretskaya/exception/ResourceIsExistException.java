package by.training.zaretskaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ResourceIsExistException extends BadRequestException {
    private static String message = "$resource with id $id is already exist.";
    private static String identifierResource = "$resource";
    private static String identifierId = "$id";

    public ResourceIsExistException(String nameResource, String id) {
        super(message
                .replace(identifierResource, nameResource)
                .replace(identifierId, id));
    }

    public ResourceIsExistException(String message) {
        super(message);
    }
}
