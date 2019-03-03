package by.training.zaretskaya.controllers;

import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.distribution.DistributedService2;
import by.training.zaretskaya.distribution.RollbackService;
import by.training.zaretskaya.exception.FailedOperationException;
import by.training.zaretskaya.exception.SomethingWrongWithDataBaseException;
import by.training.zaretskaya.interfaces.ICollectionService;
import by.training.zaretskaya.models.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class CollectionController {

    @Autowired
    ICollectionService<Collection> collectionService;

    @Autowired
    DistributedService2 distributedService;

    @Autowired
    RollbackService rollbackService;

    //Attempt to remove path in creating location!!!
    @PostMapping
    public ResponseEntity createCollection(@RequestBody Collection collection,
                                           @RequestHeader(name = "counter", required = false,
                                                   defaultValue = "0") int counter,
                                           @RequestHeader(name = "rollback", required = false,
                                                   defaultValue = "false") boolean flagRollback) {
        if (distributedService.isMyGroup(collection.getName())) {
            rollbackService.saveResource(collection);
            try {
                collectionService.create(collection);
                distributedService.sendPostObject(collection, counter, flagRollback, collection.getName());
                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{idCollection}")
                        .buildAndExpand(collection.getName())
                        .toUri();
                return ResponseEntity.created(location).build();
            } catch (SomethingWrongWithDataBaseException | ResourceAccessException e) {
                if (e instanceof ResourceAccessException) {
                    collectionService.delete(collection.getName());
                    if (counter != 0) {
                        counter--;
                    }
                }
                rollbackService.rollback(counter, HttpMethod.POST);
                //мне пришлось здесь это поставить
                throw new FailedOperationException();
            }
        } else {
            return distributedService.redirectPost(collection);
        }
    }

    @GetMapping
    public List<Collection> listCollections
            (@RequestParam(required = false,
                    defaultValue = Constants.START_PAGE) int page,
             @RequestParam(required = false,
                     defaultValue = Constants.DEFAULT_LIMIT_SIZE) int size) {
        return collectionService.listCollections(page, size);
    }

    @GetMapping("/{idCollection}")
    public Collection getCollectionById(@PathVariable String idCollection,
                                        @RequestHeader(name = "counter", required = false,
                                                defaultValue = "0") int counter) {
        if (distributedService.isMyGroup(idCollection)) {
            try {
                return collectionService.getById(idCollection);
            } catch (SomethingWrongWithDataBaseException e) {
                if (distributedService.groupConsistReplicas(idCollection)) {
                    return (Collection) distributedService.sendGetObject(counter, idCollection);
                } else {
                    throw e;
                }
            }
        } else {
            return (Collection) distributedService.redirect(idCollection, HttpMethod.GET);
        }
    }

    @DeleteMapping("/{idCollection}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCollection(@PathVariable String idCollection,
                                 @RequestHeader(name = "counter", required = false,
                                         defaultValue = "0") int counter,
                                 @RequestHeader(name = "rollback", required = false,
                                         defaultValue = "false") boolean flagRollback)
            throws CloneNotSupportedException {
        if (distributedService.isMyGroup(idCollection)) {
            rollbackService.saveResource(collectionService.getById(idCollection).clone());
            try {
                collectionService.delete(idCollection);
                distributedService.sendDeleteObject(counter, flagRollback, idCollection);
            } catch (SomethingWrongWithDataBaseException | ResourceAccessException e) {
                if (e instanceof ResourceAccessException) {
                    collectionService.create((Collection) rollbackService.getResource());
                    if (counter != 0) {
                        counter--;
                    }
                }
                rollbackService.rollback(counter, HttpMethod.DELETE, idCollection);
            }
        } else {
            distributedService.redirect(idCollection, HttpMethod.DELETE);
        }
    }

    @PutMapping("/{idCollection}/name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollectionName(@PathVariable String idCollection,
                                     @RequestBody Collection collection,
                                     @RequestHeader(name = "counter", required = false,
                                             defaultValue = "0") int counter,
                                     @RequestHeader(name = "rollback", required = false,
                                             defaultValue = "false") boolean flagRollback) {
        if (distributedService.isMyGroup(idCollection)) {
            try {
                collectionService.updateName(idCollection, collection.getName());
                distributedService.sendUpdateObject(collection, counter, flagRollback,
                        idCollection, Constants.VARIABLE_FIELD_NAME);
            } catch (SomethingWrongWithDataBaseException | ResourceAccessException e) {
                if (e instanceof ResourceAccessException) {
                    collectionService.updateName(collection.getName(), idCollection);
                    if (counter != 0) {
                        counter--;
                    }
                }
                rollbackService.rollback(counter, HttpMethod.PUT, idCollection, Constants.VARIABLE_FIELD_NAME);
            }
        } else {
            distributedService.redirectPut(idCollection, collection, Constants.VARIABLE_FIELD_NAME);
        }
    }

    @PutMapping("/{idCollection}/limit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollectionCacheLimit(@PathVariable String idCollection,
                                           @RequestBody Collection collection,
                                           @RequestHeader(name = "counter", required = false,
                                                   defaultValue = "0") int counter,
                                           @RequestHeader(name = "rollback", required = false,
                                                   defaultValue = "false") boolean flagRollback)
            throws CloneNotSupportedException {
        if (distributedService.isMyGroup(idCollection)) {
            rollbackService.saveResource(collectionService.getById(idCollection).clone());
            try {
                collectionService.updateCacheLimit(idCollection, collection.getCacheLimit());
                distributedService.sendUpdateObject(collection, counter, flagRollback,
                        idCollection, Constants.VARIABLE_FIELD_LIMIT);
            } catch (SomethingWrongWithDataBaseException | ResourceAccessException e) {
                if (e instanceof ResourceAccessException) {
                    collectionService.updateCacheLimit(idCollection,
                            ((Collection) rollbackService.getResource()).getCacheLimit());
                    if (counter != 0) {
                        counter--;
                    }
                }
                rollbackService.rollback(counter, HttpMethod.PUT, idCollection, Constants.VARIABLE_FIELD_LIMIT);
            }
        } else {
            distributedService.redirectPut(idCollection, collection, Constants.VARIABLE_FIELD_LIMIT);
        }
    }

    @PutMapping("/{idCollection}/algorithm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollectionAlgorithm(@PathVariable String idCollection,
                                          @RequestBody Collection collection,
                                          @RequestHeader(name = "counter", required = false,
                                                  defaultValue = "0") int counter,
                                          @RequestHeader(name = "rollback", required = false,
                                                  defaultValue = "false") boolean flagRollback)
            throws CloneNotSupportedException {
        if (distributedService.isMyGroup(idCollection)) {
            rollbackService.saveResource(collectionService.getById(idCollection).clone());
            try {
                collectionService.updateAlgorithm(idCollection, collection.getAlgorithm());
                distributedService.sendUpdateObject(collection, counter, flagRollback,
                        idCollection, Constants.VARIABLE_FIELD_ALGORITHM);
            } catch (SomethingWrongWithDataBaseException | ResourceAccessException e) {
                if (e instanceof ResourceAccessException) {
                    collectionService.updateAlgorithm(idCollection,
                            ((Collection) rollbackService.getResource()).getAlgorithm());
                    if (counter != 0) {
                        counter--;
                    }
                }
                rollbackService.rollback(counter, HttpMethod.PUT, idCollection, Constants.VARIABLE_FIELD_ALGORITHM);
            }
        } else {
            distributedService.redirectPut(idCollection, collection, Constants.VARIABLE_FIELD_ALGORITHM);
        }
    }
}
