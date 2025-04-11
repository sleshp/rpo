package ru.bmstu.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.bmstu.backend.models.Painting;
import ru.bmstu.backend.repositories.PaintingRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/paintings")
public class PaintingController {
    @Autowired
    private PaintingRepository paintingRepository;

    @GetMapping
    public List<Painting> getAllPaintings() {
        return paintingRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Painting> createPainting(@RequestBody Painting painting) {
        try {
            Painting savedPainting = paintingRepository.save(painting);
            return new ResponseEntity<>(savedPainting, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Painting> updatePainting(@PathVariable int id, @RequestBody Painting painting) {
        Painting paintingObj;
        Optional<Painting> optionalPainting = paintingRepository.findById(id);
        if (optionalPainting.isPresent()) {
            paintingObj = optionalPainting.get();
            paintingObj.setMuseum(painting.getMuseum());
            paintingObj.setArtist(painting.getArtist());
            paintingObj.setYear(painting.getYear());
            paintingObj.setName(painting.getName());
            paintingRepository.save(paintingObj);
            return ResponseEntity.ok(paintingObj);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Painting not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Painting> deletePainting(@PathVariable int id) {
        Optional<Painting> optionalPainting = paintingRepository.findById(id);
        if (optionalPainting.isPresent()) {
            paintingRepository.delete(optionalPainting.get());
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Painting not found");
        return ResponseEntity.ok(optionalPainting.get());
    }

}
