package by.training.zaretskaya.controllers;

import by.training.zaretskaya.config.Configuration;
import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.distribution.DistributedService;
import by.training.zaretskaya.distribution.RollbackService;
import by.training.zaretskaya.exception.FailedOperationException;
import by.training.zaretskaya.exception.SomethingWrongWithDataBaseException;
import by.training.zaretskaya.interfaces.ICollectionService;
import by.training.zaretskaya.models.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest")
public class CollectionController {
    private static final Logger log = LogManager.getLogger(CollectionController.class);


    @Autowired
    ICollectionService<Collection> collectionService;

    @Autowired
    DistributedService distributedService;

    @Autowired
    RollbackService rollbackService;


    @GetMapping("/{idCollection}")
    public Collection getCollectionById(@PathVariable String idCollection,
                                        @RequestHeader(name = "replica", required = false, defaultValue = "false")
                                                boolean flagReplica) {
        if (distributedService.isMyGroup(idCollection)) {
            try {
                Collection collection = collectionService.getById(idCollection);
                log.info("Method GET is successfully executed in " + Configuration.getCurrentNode().getName());
                return collection;
            } catch (SomethingWrongWithDataBaseException e) {
                log.error("Problem with Data Base in " + Configuration.getCurrentNode().getName(), e);
                if (!flagReplica) {
                    return (Collection) distributedService.sendGetObject(Collection.class, idCollection);
                } else {
                    throw new FailedOperationException();
                }
            }
        } else {
            return (Collection) distributedService.redirectGet(Collection.class, idCollection);
        }
    }

    @PostMapping
    public ResponseEntity createCollection(@RequestBody Collection collection,
                                           @RequestHeader(name = "counter", required = false,
                                                   defaultValue = "0") int counter,
                                           @RequestHeader(name = "rollback", required = false,
                                                   defaultValue = "false") boolean flagRollback) {
        if (distributedService.isMyGroup(collection.getName())) {
            try {
                collectionService.create(collection);
                log.info("Method POST is successfully executed in " + Configuration.getCurrentNode().getName());
                distributedService.sendPostObject(collection, counter, flagRollback, collection.getName());
                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{idCollection}")
                        .buildAndExpand(collection.getName())
                        .toUri();
                return ResponseEntity.created(location).build();
            } catch (SomethingWrongWithDataBaseException | FailedOperationException e) {
                if (e instanceof FailedOperationException) {
                    log.warn("Starting rollback for POST request in current node");
                    collectionService.delete(collection.getName());
                } else {
                    log.error("Problem with Data Base in  " + Configuration.getCurrentNode().getName(), e);
                }
                rollbackService.rollback(counter, collection.getName());
                //мне пришлось здесь это поставить
                throw new FailedOperationException();
            }
        } else {
            return distributedService.redirectPost(collection, collection.getName());
        }
    }

    @PutMapping("/{idCollection}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollection(@PathVariable String idCollection,
                                 @RequestBody Collection collection,
                                 @RequestHeader(name = "counter", required = false, defaultValue = "0") int counter,
                                 @RequestHeader(name = "rollback", required = false, defaultValue = "false")
                                         boolean flagRollback)
            throws CloneNotSupportedException {
        if (distributedService.isMyGroup(idCollection)) {
            //Instead Validator in Service
            Collection collectionOldValue = collectionService.getById(idCollection).clone();
            try {
                collectionService.update(idCollection, collection);
                log.info("Method PUT is successfully executed in " + Configuration.getCurrentNode().getName());
                distributedService.sendUpdateObject(collection, counter, flagRollback, idCollection);
            } catch (SomethingWrongWithDataBaseException | FailedOperationException e) {
                if (flagRollback) {
                    log.fatal("Problem with rollback. The application doesn't work correctly", e);
                    throw new FailedOperationException();
                }
                if (e instanceof FailedOperationException) {
                    log.warn("Starting rollback for PUT request in current node");
                    collectionService.update(idCollection, collectionOldValue);
                } else {
                    log.error("Problem with Data Base in  " + Configuration.getCurrentNode().getName(), e);
                }
                rollbackService.rollback(collectionOldValue, counter, HttpMethod.PUT, idCollection);
            }
        } else {
            distributedService.redirectPut(collection, idCollection);
        }
    }

    @DeleteMapping("/{idCollection}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCollection(@PathVariable String idCollection,
                                 @RequestHeader(name = "counter", required = false, defaultValue = "0") int counter,
                                 @RequestHeader(name = "rollback", required = false, defaultValue = "false")
                                         boolean flagRollback)
            throws CloneNotSupportedException {
        if (distributedService.isMyGroup(idCollection)) {
            Collection collectionOldValue = collectionService.getById(idCollection).clone();
            try {
                log.warn("User is going to delete collection with id " + idCollection);
                collectionService.delete(idCollection);
                log.info("Method DELETE is successfully executed");
                distributedService.sendDeleteObject(counter, flagRollback, idCollection);
            } catch (SomethingWrongWithDataBaseException | FailedOperationException e) {
                if (flagRollback) {
                    log.fatal("Problem with rollback. The application doesn't work correctly", e);
                    throw new FailedOperationException();
                }
                if (e instanceof FailedOperationException) {
                    log.warn("Starting rollback for DELETE request in current node");
                    collectionService.create(collectionOldValue);
                } else {
                    log.error("Problem with Data Base in  " + Configuration.getCurrentNode().getName(), e);
                }
                rollbackService.rollback(collectionOldValue, counter, HttpMethod.DELETE, collectionOldValue.getName());
            }
        } else {
            distributedService.redirectDelete(idCollection);
        }
    }

    @GetMapping
    public List<Collection> listCollections
            (@RequestParam(required = false,
                    defaultValue = Constants.DEFAULT_OBJECT_TO_COMPARE) String compare,
             @RequestParam(required = false,
                     defaultValue = Constants.DEFAULT_LIMIT_SIZE) int size,
             @RequestHeader(name = "main", required = false,
                     defaultValue = "true") boolean mainGroup,
             @RequestHeader(name = "replica", required = false,
                     defaultValue = "false") boolean flagReplica) {
        List<Collection> collections;
        try {
            collections = collectionService.listCollections(compare, size);
        } catch (SomethingWrongWithDataBaseException e) {
            log.error("Problem with Data Base in  " + Configuration.getCurrentNode().getName(), e);
            //Чтоб не стучаться дальше
            if (flagReplica) {
                throw new FailedOperationException();
            }
            collections = distributedService.sendListToReplica(compare, size)
                    .stream().map((obj) -> (Collection) obj).collect(Collectors.toList());
        }
        log.info("Method LIST is successfully executed in group " + Configuration.getCurrentNode().getIdGroup());
        if (mainGroup) {
            log.debug("List was received with " + collections.size() + " size from " +
                    Configuration.getCurrentNode().getName());
            collections = distributedService.redirectListCollection(compare, size, collections);
        }
        return collections;
    }
}
