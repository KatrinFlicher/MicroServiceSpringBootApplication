package by.training.zaretskaya.models;


import by.training.zaretskaya.cache.ICache;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@Entity
@Table(name = "collection")
@NamedQuery(name = "Collection.findAllCollections",
        query = "SELECT c from Collection c where c.name > :name order by c.name")
public class Collection implements Cloneable {
    @Id
    private String name;
    @Column(name = "algorithm")
    private String cacheAlgorithm;

    @JsonIgnore
    @Transient
    private ICache<String, Document> cache;

    @Column(name = "cache_limit")
    private int cacheLimit;

    @Column(name = "json_scheme")
    @Type(type = "text")
    private String jsonScheme;

    public Collection(String name, int cacheLimit, String algorithm, String jsonScheme) {
        this.name = name;
        this.cacheLimit = cacheLimit;
        this.cacheAlgorithm = algorithm;
        this.jsonScheme = jsonScheme;
    }

    public Collection(Collection collection) {
        this(collection.getName(), collection.getCacheLimit(), collection.getCacheAlgorithm(), collection.getJsonScheme());
    }

    public Collection() {
    }
}
