package ru.bmstu.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.bmstu.backend.models.Museum;
import ru.bmstu.backend.repositories.MuseumRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/museums")
public class MuseumController {
    @Autowired
    private MuseumRepository museumRepository;

    @GetMapping
    public List<Museum> getAllMuseums() {
        return museumRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Museum> createMuseum(@RequestBody Museum museum) {
        try{
            Museum createdMuseum = museumRepository.save(museum);
            return new ResponseEntity<>(createdMuseum, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Museum> updateMuseum(@RequestBody Museum museum, @PathVariable int id) {
        Museum museumObj;
        Optional<Museum> optionalMuseum = museumRepository.findById(id);
        if (optionalMuseum.isPresent()) {
            museumObj = optionalMuseum.get();
            museumObj.setName(museum.getName());
            museumObj.setLocation(museum.getLocation());
            museumRepository.save(museumObj);
            return ResponseEntity.ok(museumObj);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Museum not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Museum> deleteMuseum(@PathVariable int id) {
        Optional<Museum> optionalMuseum = museumRepository.findById(id);
        if (optionalMuseum.isPresent()) {
            museumRepository.delete(optionalMuseum.get());
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Museum not found");
        return ResponseEntity.ok(optionalMuseum.get());
    }
}
