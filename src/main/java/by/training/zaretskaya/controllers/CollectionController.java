package by.training.zaretskaya.controllers;

import by.training.zaretskaya.interfaces.ICollectionService;
import by.training.zaretskaya.models.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class CollectionController {

    @Autowired
    ICollectionService<Collection> collectionService;

    @PostMapping
    public ResponseEntity createCollection(@RequestBody Collection collection) {
        collectionService.create(collection);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{idCollection}")
                .buildAndExpand(collection.getName())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public List<Collection> listCollections(@RequestParam(required = false, defaultValue = "1") int page,
                                            @RequestParam(required = false, defaultValue = "5") int size) {
        return collectionService.listCollections(page, size);
    }

    @GetMapping("/{idCollection}")
    public Collection getCollectionById(@PathVariable String idCollection) {
        return collectionService.getById(idCollection);
    }

    @DeleteMapping("/{idCollection}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCollection(@PathVariable String idCollection) {
        collectionService.delete(idCollection);
    }

    @PutMapping("/{idCollection}/name")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollectionName(@PathVariable String idCollection,
                                     @RequestBody Collection collection) {
        collectionService.updateName(idCollection, collection.getName());
    }

    @PutMapping("/{idCollection}/limit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollectionCacheLimit(@PathVariable String idCollection,
                                           @RequestBody Collection collection) {
        collectionService.updateCacheLimit(idCollection, collection.getCacheLimit());
    }

    @PutMapping("/{idCollection}/algorithm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCollectionAlgorithm(@PathVariable String idCollection,
                                          @RequestBody Collection collection) {
        collectionService.updateAlgorithm(idCollection, collection.getAlgorithm());
    }
}
