package by.training.zaretskaya.models;

public class Document {
    private String key;
    private Object value;

    public Document() {
    }

    public Document(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
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
