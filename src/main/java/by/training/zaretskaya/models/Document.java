package by.training.zaretskaya.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Document {
    private String key;
    private String value;

    public Document() {
    }

    public Document(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "Document{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
