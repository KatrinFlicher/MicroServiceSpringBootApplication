package by.training.zaretskaya.controllers;


import by.training.zaretskaya.config.Configuration;
import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.distribution.DistributedService;
import by.training.zaretskaya.distribution.RollbackService;
import by.training.zaretskaya.exception.FailedOperationException;
import by.training.zaretskaya.exception.SomethingWrongWithDataBaseException;
import by.training.zaretskaya.interfaces.IDocumentService;
import by.training.zaretskaya.models.Document;
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
@RequestMapping("/rest/{idCollection}/docs")
public class DocumentController {
    private static final Logger log = LogManager.getLogger(DocumentController.class);

    @Autowired
    IDocumentService<Document> documentService;

    @Autowired
    DistributedService distributedService;

    @Autowired
    RollbackService rollbackService;

    @GetMapping("/{idDoc}")
    Document getDocument(@PathVariable String idCollection,
                         @PathVariable String idDoc,
                         @RequestHeader(name = "replica", required = false, defaultValue = "false")
                                 boolean flagReplica) throws ClassNotFoundException {
        if (distributedService.isMyGroup(idCollection)) {
            try {
                Document document = documentService.get(idCollection, idDoc);
                log.info("Method GET is successfully executed in " + Configuration.getCurrentNode().getName());
                return document;
            } catch (SomethingWrongWithDataBaseException e) {
                log.error("Problem with Data Base in " + Configuration.getCurrentNode().getName(), e);
                if (!flagReplica) {
                    return (Document) distributedService.sendGetObject(Document.class.getName(), idCollection, idDoc);
                } else {
                    throw new FailedOperationException();
                }
            }
        } else {
            return (Document) distributedService.redirectGet(Document.class.getName(), idCollection, idDoc);
        }
    }

    @PostMapping
    public ResponseEntity createDocument(@PathVariable String idCollection,
                                         @RequestBody Document document,
                                         @RequestHeader(name = "counter", required = false,
                                                 defaultValue = "0") int counter,
                                         @RequestHeader(name = "rollback", required = false,
                                                 defaultValue = "false") boolean flagRollback) {
        if (distributedService.isMyGroup(idCollection)) {
            try {
                documentService.create(idCollection, document);
                log.info("Method POST is successfully executed in " + Configuration.getCurrentNode().getName());
                distributedService.sendPostObject(document, counter, flagRollback, idCollection);
                URI location = ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .path("/{idDoc}")
                        .buildAndExpand(document.getKey())
                        .toUri();
                return ResponseEntity.created(location).build();
            } catch (SomethingWrongWithDataBaseException | FailedOperationException e) {
                if (e instanceof FailedOperationException) {
                    log.warn("Starting rollback for POST request in current node");
                    documentService.delete(idCollection, document.getKey());
                } else {
                    log.error("Problem with Data Base in  " + Configuration.getCurrentNode().getName(), e);
                }
                rollbackService.rollback(counter, idCollection, document.getKey());
                throw new FailedOperationException();
            }
        } else {
            return distributedService.redirectPost(document, idCollection);
        }
    }

    @PutMapping("/{idDoc}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateDocument(@PathVariable String idCollection,
                        @PathVariable String idDoc,
                        @RequestBody Document document,
                        @RequestHeader(name = "counter", required = false, defaultValue = "0") int counter,
                        @RequestHeader(name = "rollback", required = false, defaultValue = "false") boolean flagRollback)
            throws CloneNotSupportedException {
        if (distributedService.isMyGroup(idCollection)) {
            Document documentOldValue = documentService.get(idCollection, idDoc).clone();
            try {
                documentService.update(idCollection, idDoc, document);
                log.info("Method PUT is successfully executed in " + Configuration.getCurrentNode().getName());
                distributedService.sendUpdateObject(document, counter, flagRollback, idCollection, idDoc);
            } catch (SomethingWrongWithDataBaseException | FailedOperationException e) {
                if (flagRollback) {
                    log.fatal("Problem with rollback. The application doesn't work correctly", e);
                    throw new FailedOperationException();
                }
                if (e instanceof FailedOperationException) {
                    log.warn("Starting rollback for PUT request in current node");
                    documentService.update(idCollection, idDoc, documentOldValue);
                } else {
                    log.error("Problem with Data Base in  " + Configuration.getCurrentNode().getName(), e);
                }
                rollbackService.rollback(documentOldValue, counter, HttpMethod.PUT, idCollection, idDoc);
            }
        } else {
            distributedService.redirectPut(document, idCollection, idDoc);
        }
    }


    @DeleteMapping("/{idDoc}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDocument(@PathVariable String idCollection,
                        @PathVariable String idDoc,
                        @RequestHeader(name = "counter", required = false, defaultValue = "0") int counter,
                        @RequestHeader(name = "rollback", required = false, defaultValue = "false")
                                boolean flagRollback) throws CloneNotSupportedException {
        if (distributedService.isMyGroup(idCollection)) {
            Document documentOldValue = documentService.get(idCollection, idDoc).clone();
            try {
                documentService.delete(idCollection, idDoc);
                log.info("Method DELETE is successfully executed");
                distributedService.sendDeleteObject(counter, flagRollback, idCollection);
            } catch (SomethingWrongWithDataBaseException | FailedOperationException e) {
                if (flagRollback) {
                    log.fatal("Problem with rollback. The application doesn't work correctly", e);
                    throw new FailedOperationException();
                }
                if (e instanceof FailedOperationException) {
                    log.warn("Starting rollback for DELETE request in current node");
                    documentService.create(idCollection, documentOldValue);
                } else {
                    log.error("Problem with Data Base in  " + Configuration.getCurrentNode().getName(), e);
                }
                rollbackService.rollback(documentOldValue, counter, HttpMethod.DELETE, idCollection);
            }
        } else {
            distributedService.redirectDelete(idCollection, idDoc);
        }
    }


    @GetMapping
    List<Document> listDocuments(@PathVariable String idCollection,
                                 @RequestParam(required = false,
                                         defaultValue = Constants.DEFAULT_OBJECT_TO_COMPARE) String compare,
                                 @RequestParam(required = false,
                                         defaultValue = Constants.DEFAULT_LIMIT_SIZE) int size,
                                 @RequestHeader(name = "replica", required = false,
                                         defaultValue = "false") boolean flagReplica) {
        List<Document> documents;
        if (distributedService.isMyGroup(idCollection)) {
            try {
                documents = documentService.list(idCollection, compare, size);
            } catch (SomethingWrongWithDataBaseException e) {
                log.error("Problem with Data Base in  " + Configuration.getCurrentNode().getName(), e);
                if (!flagReplica) {
                    documents = distributedService.sendListToReplica(compare, size, idCollection)
                            .stream().map((obj) -> (Document) obj).collect(Collectors.toList());
                } else {
                    throw new FailedOperationException();
                }
            }
        } else {
            return distributedService.redirectListDocument(idCollection, compare, size);
        }
        log.info("Method LIST Documents is successfully executed");
        return documents;
    }
}
