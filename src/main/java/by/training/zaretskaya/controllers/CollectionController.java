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
                }
                rollbackService.rollback(counter, collection.getName());
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
                return (Collection) distributedService.sendGetObject(counter, idCollection);
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
            Collection collectionOldValue = collectionService.getById(idCollection).clone();
            try {
                collectionService.delete(idCollection);
                distributedService.sendDeleteObject(counter, flagRollback, idCollection);
            } catch (SomethingWrongWithDataBaseException | ResourceAccessException e) {
                if (e instanceof ResourceAccessException) {
                    collectionService.create(collectionOldValue);
                }
                rollbackService.rollback(collectionOldValue, counter, HttpMethod.DELETE, idCollection);
            }
        } else {
            distributedService.redirect(idCollection, HttpMethod.DELETE);
        }
    }

    @PutMapping("/{idCollection}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollection(@PathVariable String idCollection,
                                     @RequestBody Collection collection,
                                     @RequestHeader(name = "counter", required = false,
                                             defaultValue = "0") int counter,
                                     @RequestHeader(name = "rollback", required = false,
                                             defaultValue = "false") boolean flagRollback) throws CloneNotSupportedException {
        if (distributedService.isMyGroup(idCollection)) {
            Collection collectionOldValue = collectionService.getById(idCollection).clone();
            try {
                collectionService.update(idCollection, collection);
                distributedService.sendUpdateObject(collection, counter, flagRollback,
                        idCollection);
            } catch (SomethingWrongWithDataBaseException | ResourceAccessException e) {
                if (e instanceof ResourceAccessException) {
                    collectionService.update(idCollection, collectionOldValue);
                }
                rollbackService.rollback(collectionOldValue, counter, HttpMethod.PUT, idCollection);
            }
        } else {
            distributedService.redirectPut(idCollection, collection);
        }
    }
}
