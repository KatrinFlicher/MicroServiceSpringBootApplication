package by.training.zaretskaya.services;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DistributedDocumentService<D> extends IDocumentService<D> {
    boolean isMyGroup(String id);
    ResponseEntity<D> redirectQuery(HttpMethod method, D entity, String... ids);
    List<D> listFromReplica(String nameCollection, String objectToCompare, int size);
}
