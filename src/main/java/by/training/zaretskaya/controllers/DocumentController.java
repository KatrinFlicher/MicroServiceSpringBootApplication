package by.training.zaretskaya.controllers;


import by.training.zaretskaya.interfaces.IDocumentService;
import by.training.zaretskaya.models.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/rest/{idCollection}/docs")
public class DocumentController {

    @Autowired
    IDocumentService documentService;

    @PostMapping
    public ResponseEntity createDocument(@PathVariable String idCollection, @RequestBody Document document) {
        documentService.create(idCollection, document);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{idDoc}")
                .buildAndExpand(document.getKey())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{idDoc}")
    Object getDocument(@PathVariable String idCollection, @PathVariable String idDoc) {
        return documentService.get(idCollection, idDoc).getValue();
    }

    @DeleteMapping("/{idDoc}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteDocument(@PathVariable String idCollection, @PathVariable String idDoc) {
        documentService.delete(idCollection, idDoc);

    }

    @PutMapping("/{idDoc}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateDocument(@PathVariable String idCollection,
                        @PathVariable String idDoc,
                        @RequestBody Document document) {
        documentService.update(idCollection, idDoc, document);

    }

    @GetMapping
    List listDocuments(@PathVariable String idCollection,
                       @RequestParam(required = false, defaultValue = "1") int page,
                       @RequestParam(required = false, defaultValue = "10") int size) {

        return documentService.list(idCollection, page, size);
    }

}
