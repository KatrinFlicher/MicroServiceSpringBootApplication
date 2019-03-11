package by.training.zaretskaya.models;


import by.training.zaretskaya.interfaces.ICache;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "collection")
@NamedQuery(name = "Collection.findAllCollections",
        query = "SELECT c from Collection c where c.name > :name order by c.name")
public class Collection implements Cloneable {
    @JsonIgnore
    @Transient
    private ICache<String, Document> cache;
    @Id
    private String name;
    @Column(name = "cache_limit")
    private int cacheLimit;
    @Column(name = "algorithm")
    private String algorithm;

    @Column(name = "json_scheme")
    @Type(type = "text")
    private String jsonScheme;

    public Collection(String name, int cacheLimit, String algorithm, String jsonScheme) {
        this.name = name;
        this.cacheLimit = cacheLimit;
        this.algorithm = algorithm;
        this.jsonScheme = jsonScheme;
    }

    public Collection() {
    }

    public ICache<String, Document> getCache() {
        return cache;
    }

    public void setCache(ICache<String, Document> cache) {
        this.cache = cache;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCacheLimit() {
        return cacheLimit;
    }

    public void setCacheLimit(int cacheLimit) {
        this.cacheLimit = cacheLimit;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    @JsonIgnore
    public String getJsonScheme() {
        return jsonScheme;
    }

    public void setJsonScheme(String jsonScheme) {
        this.jsonScheme = jsonScheme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collection that = (Collection) o;
        return getName() != null ? getName().equals(that.getName()) : that.getName() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Collection{" +
                "cache=" + cache +
                ", name='" + name + '\'' +
                ", cacheLimit=" + cacheLimit +
                ", algorithm='" + algorithm + '\'' +
                ", jsonScheme='" + jsonScheme + '\'' +
                '}';
    }

    public Collection clone() throws CloneNotSupportedException {
        return (Collection) super.clone();
    }
}
