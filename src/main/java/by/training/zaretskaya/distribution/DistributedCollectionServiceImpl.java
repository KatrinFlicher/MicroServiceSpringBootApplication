package by.training.zaretskaya.distribution;

import by.training.zaretskaya.config.Node;
import by.training.zaretskaya.exception.FailedOperationException;
import by.training.zaretskaya.interfaces.DistributedCollectionService;
import by.training.zaretskaya.interfaces.ICollectionService;
import by.training.zaretskaya.models.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.Map;

@Service
public class DistributedCollectionServiceImpl extends DistributedServiceBase<Collection>
        implements DistributedCollectionService<Collection> {

    private static final Logger log = LogManager.getLogger(DistributedCollectionServiceImpl.class);

    private ICollectionService<Collection> collectionService;
    private Map<String, Node> nodesAll;

    @Autowired
    public DistributedCollectionServiceImpl(@Qualifier("nodes") Map<String, Node> nodesAll,
                                            @Qualifier("CollectionService") ICollectionService<Collection> collectionService) {
        super(Collection.class);
        this.collectionService = collectionService;
        log.info(nodesAll);
        this.nodesAll = nodesAll;
    }

    @Override
    public void create(Collection collection) {
        try {
            callNodes(nodesAll.values(), HttpMethod.POST, null, collection);
        } catch (FailedOperationException e) {
            collectionService.delete(collection.getName());
            throw new FailedOperationException();
        }
    }

    @Override
    public Collection getById(String name) {
        return get(nodesAll.values(), name).getBody();
    }

    @Override
    public void delete(String name) {
        Collection collectionOldValue = new Collection(collectionService.getById(name));
        try {
            callNodes(nodesAll.values(), HttpMethod.DELETE, collectionOldValue, null, name);
        } catch (FailedOperationException e) {
            collectionService.create(collectionOldValue);
            throw new FailedOperationException();
        }
    }

    @Override
    public void update(String name, Collection collection) {
        log.info("ALL NODES" + nodesAll);
        Collection collectionOldValue = new Collection(collectionService.getById(name));
        try {
            callNodes(nodesAll.values(), HttpMethod.PUT, collectionOldValue, collection, name);
        } catch (FailedOperationException e) {
            collectionService.update(name, collectionOldValue);
            throw new FailedOperationException();
        }
    }

    @Override
    public List<Collection> listCollections(String objectToCompare, int size) {
        for (Node node : nodesAll.values()) {
            try {
                ResponseEntity<List<Collection>> entity = new ListRestCommand<>(HttpMethod.GET,
                        Collection.class, node.getHost(), objectToCompare, size)
                        .executeList();
                log.info("Response is received from " + node.getHost());
                return entity.getBody();
            } catch (ResourceAccessException | HttpServerErrorException.ServiceUnavailable e) {
                log.error("Node " + node.getName() + " is unavailable.", e);
            }
        }
        throw new FailedOperationException();
    }
}
