package by.training.zaretskaya.models;

import lombok.Data;

@Data
public class Document implements Cloneable {
    private String key;
    private String value;

    public Document() {
    }

    public Document(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Document(Document document) {
        this(document.getKey(), document.getValue());
    }
}
