package by.training.zaretskaya.controllers;

import by.training.zaretskaya.config.Node;
import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.FailedOperationException;
import by.training.zaretskaya.exception.SomethingWrongWithDataBaseException;
import by.training.zaretskaya.services.DistributedCollectionService;
import by.training.zaretskaya.services.ICollectionService;
import by.training.zaretskaya.models.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class CollectionController {
    private static final Logger log = LogManager.getLogger(CollectionController.class);

    private ICollectionService<Collection> collectionService;
    private DistributedCollectionService<Collection> distributedService;
    private Node node;

    @Autowired
    public CollectionController(@Qualifier("CollectionService") ICollectionService<Collection> collectionService,
                                Node node,
                                DistributedCollectionService<Collection> distributedService) {
        this.collectionService = collectionService;
        this.node = node;
        this.distributedService = distributedService;
    }

    @GetMapping("/{idCollection}")
    public Collection getCollectionById(@PathVariable String idCollection,
                                        @RequestHeader(name = "replica", required = false, defaultValue = "false")
                                                boolean flagReplica) {
        try {
            Collection collection = collectionService.getById(idCollection);
            log.info("Method GET is successfully executed in " + node.getName());
            return collection;
        } catch (SomethingWrongWithDataBaseException e) {
            if (!flagReplica) {
                return distributedService.getById(idCollection);
            }
            throw new FailedOperationException();
        }
    }

    @PostMapping
    public ResponseEntity createCollection(@RequestBody Collection collection,
                                           @RequestHeader(name = "replica", required = false, defaultValue = "false")
                                                   boolean flagReplica) {
        collectionService.create(collection);
        log.info("Method POST is successfully executed in " + node.getName());
        if (!flagReplica) {
            distributedService.create(collection);
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{idCollection}")
                .buildAndExpand(collection.getName())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{idCollection}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollection(@PathVariable String idCollection,
                                 @RequestBody Collection collection,
                                 @RequestHeader(name = "replica", required = false, defaultValue = "false")
                                         boolean flagReplica) {
        collectionService.update(idCollection, collection);
        log.info("Method PUT is successfully executed in " + node.getName());
        if (!flagReplica) {
            distributedService.update(idCollection, collection);
        }
    }

    @DeleteMapping("/{idCollection}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCollection(@PathVariable String idCollection,
                                 @RequestHeader(name = "replica", required = false, defaultValue = "false")
                                         boolean flagReplica) {
        log.warn("User is going to delete collection with id " + idCollection);
        collectionService.delete(idCollection);
        log.info("Method DELETE is successfully executed in " + node.getName());
        if (!flagReplica) {
            distributedService.delete(idCollection);
        }
    }

    @GetMapping
    public List<Collection> listCollections
            (@RequestParam(required = false,
                    defaultValue = Constants.DEFAULT_OBJECT_TO_COMPARE) String compare,
             @RequestParam(required = false,
                     defaultValue = Constants.DEFAULT_LIMIT_SIZE) int size,
             @RequestHeader(name = "replica", required = false, defaultValue = "false")
                     boolean flagReplica) {
        try {
            List<Collection> collections = collectionService.listCollections(compare, size);
            log.info("Method LIST is successfully executed in " + node.getName());
            return collections;
        } catch (SomethingWrongWithDataBaseException e) {
            if (!flagReplica) {
                return distributedService.listCollections(compare, size);
            }
            throw new FailedOperationException();
        }
    }
}
