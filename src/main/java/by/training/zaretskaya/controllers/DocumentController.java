package by.training.zaretskaya.controllers;


import by.training.zaretskaya.config.Node;
import by.training.zaretskaya.constants.Constants;
import by.training.zaretskaya.exception.FailedOperationException;
import by.training.zaretskaya.exception.SomethingWrongWithDataBaseException;
import by.training.zaretskaya.services.DistributedDocumentService;
import by.training.zaretskaya.services.IDocumentService;
import by.training.zaretskaya.models.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/rest/{idCollection}/docs")
public class DocumentController {
    private static final Logger log = LogManager.getLogger(DocumentController.class);

    private IDocumentService<Document> documentService;
    private DistributedDocumentService<Document> distributedService;
    private Node node;

    @Autowired
    public DocumentController(@Qualifier("DocumentService") IDocumentService<Document> documentService,
                              DistributedDocumentService<Document> distributedService, Node node) {
        this.documentService = documentService;
        this.distributedService = distributedService;
        this.node = node;
    }

    @GetMapping("/{idDoc}")
    Document getDocument(@PathVariable String idCollection,
                         @PathVariable String idDoc,
                         @RequestHeader(name = "replica", required = false, defaultValue = "false")
                                 boolean flagReplica) {
        if (distributedService.isMyGroup(idDoc)) {
            try {
                return documentService.get(idCollection, idDoc);
            } catch (SomethingWrongWithDataBaseException e) {
                log.error("Problem with Data Base in  " + node.getName(), e);
                if (!flagReplica) {
                    return distributedService.get(idCollection, idDoc);
                }
                throw new FailedOperationException();
            }
        } else {
            return distributedService.redirectQuery(HttpMethod.GET, null, idCollection, idDoc).getBody();
        }
    }

    @PostMapping
    public ResponseEntity createDocument(@PathVariable String idCollection,
                                         @RequestBody Document document,
                                         @RequestHeader(name = "replica", required = false, defaultValue = "false")
                                                 boolean flagReplica) {
        if (distributedService.isMyGroup(document.getKey())) {
            documentService.create(idCollection, document);
            if (!flagReplica) {
                distributedService.create(idCollection, document);
            }
        } else {
            distributedService.redirectQuery(HttpMethod.POST, document, idCollection);
        }
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{idDoc}")
                .buildAndExpand(document.getKey())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{idDoc}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateDocument(@PathVariable String idCollection,
                        @PathVariable String idDoc,
                        @RequestBody Document document,
                        @RequestHeader(name = "replica", required = false, defaultValue = "false")
                                boolean flagReplica) {
        if (distributedService.isMyGroup(idDoc)) {
            documentService.update(idCollection, idDoc, document);
            if (!flagReplica) {
                distributedService.update(idCollection, idDoc, document);
            }
        } else {
            distributedService.redirectQuery(HttpMethod.PUT, document, idCollection, idDoc);
        }
    }

    @DeleteMapping("/{idDoc}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDocument(@PathVariable String idCollection,
                        @PathVariable String idDoc,
                        @RequestHeader(name = "replica", required = false, defaultValue = "false")
                                boolean flagReplica) {
        if (distributedService.isMyGroup(idDoc)) {
            documentService.delete(idCollection, idDoc);
            if (!flagReplica) {
                distributedService.delete(idCollection, idDoc);
            }
        } else {
            distributedService.redirectQuery(HttpMethod.DELETE, null, idCollection, idDoc);
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
        try {
            documents = documentService.list(idCollection, compare, size);
        } catch (SomethingWrongWithDataBaseException e) {
            log.error("Problem with Data Base in  " + node.getName(), e);
            if (!flagReplica) {
                return distributedService.listFromReplica(idCollection, compare, size);
            } else {
                throw new FailedOperationException();
            }
        }
        if (!flagReplica) {
            documents.addAll(distributedService.list(idCollection, compare, size));
            documents.sort(Comparator.comparing(Document::getKey));
            log.info("Method LIST is successfully executed");
            return documents.subList(0, size);
        }
        return documents;
    }
}
