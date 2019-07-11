package by.training.zaretskaya.models;

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
        this.key = document.getKey();
        this.value = document.getValue();
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

    public Document clone() throws CloneNotSupportedException {
        return (Document) super.clone();
    }
}
