package by.training.zaretskaya.exception;

public class DocumentNotFoundException extends ResourceNotFoundException {
    public DocumentNotFoundException(String nameResource, String id) {
        super(nameResource, id);
    }
}
