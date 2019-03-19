package by.training.zaretskaya.exception;

import by.training.zaretskaya.constants.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class DocumentIsInvalidUnderTheScheme extends BadRequestException {

    public DocumentIsInvalidUnderTheScheme() {
        super(Constants.DOCUMENT_IS_INVALID_UNDER_THE_SCHEME);
    }
}
