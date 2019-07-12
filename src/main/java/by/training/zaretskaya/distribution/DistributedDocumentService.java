package by.training.zaretskaya.distribution;

import by.training.zaretskaya.services.IDocumentService;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * A class implements the DistributedDocumentService interface to
 * indicate main purpose of this class is distribution of document's entity by replicas.
 *
 * @author Zaretskaya Katsiaryna
 * @version 1.0
 */
public interface DistributedDocumentService<D> extends IDocumentService<D> {
    /**
     * Returns {true} if id refers to the group of current node.
     *
     * @param id - identifier(name) of document instance
     */
    boolean isMyGroup(String id);

    /**
     * Redirect request to the appropriate group of nodes.
     *
     * @param method - HttpMethod of request
     * @param entity - entity included in the request's body
     * @param ids    - identifiers of collection or collection/document instance for building necessary URL
     */
    ResponseEntity<D> redirectRequest(HttpMethod method, @Nullable D entity, String... ids);

    /**
     *  Gather list of documents for the specified table from every group of nodes compared with
     *  the specified object and limited in specified size and then sort and reduce received list to specified size
     *
     * @param nameCollection  - name of table collection
     * @param objectToCompare - object to compare with
     * @param size            - quantity of output collections
     */
    List<D> listFromAllGroups(String nameCollection, String objectToCompare, int size);
}
