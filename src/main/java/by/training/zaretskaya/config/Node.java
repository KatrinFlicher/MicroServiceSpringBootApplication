package by.training.zaretskaya.config;

public class Node {
    private String name;
    private String host;
    private Integer idGroup;

    public Node(String name, String host, Integer idGroup) {
        this.name = name;
        this.host = host;
        this.idGroup = idGroup;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(Integer idGroup) {
        this.idGroup = idGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", idGroup=" + idGroup +
                '}';
    }
}
