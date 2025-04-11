package ru.bmstu.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.bmstu.backend.models.Country;
import ru.bmstu.backend.repositories.CountryRepository;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class CountryController {

    @Autowired
    CountryRepository countryRepository;

    @GetMapping("/countries")
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    @PostMapping("/countries")
    public ResponseEntity<Object> createCountry(@RequestBody Country country) {
        try {
            Country nc = countryRepository.save(country);
            return ResponseEntity.ok(nc);
        } catch (Exception ex) {
            String error = ex.getMessage().contains("countries.name_UNIQUE") ? "countyalreadyexists" : "undefinederror";
            Map<String, String> map = new HashMap<>();
            map.put("error", error);
            return ResponseEntity.ok(map);
        }
    }

    @PutMapping("/countries/{id}")
    public ResponseEntity<Country> updateCountry(@PathVariable(value = "id") Long countryId,
                                                 @RequestBody Country countryDetails) {
        Optional<Country> cc = countryRepository.findById(countryId);
        if (cc.isPresent()) {
            Country country = cc.get();
            country.name = countryDetails.name;
            countryRepository.save(country);
            return ResponseEntity.ok(country);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "country not found");
        }
    }

    @DeleteMapping("/countries/{id}")
    public ResponseEntity<Object> deleteCountry(@PathVariable(value = "id") Long countryId) {
        Optional<Country> country = countryRepository.findById(countryId);
        Map<String, Boolean> resp = new HashMap<>();
        if (country.isPresent()) {
            countryRepository.delete(country.get());
            resp.put("deleted", Boolean.TRUE);
        } else {
            resp.put("deleted", Boolean.FALSE);
        }
        return ResponseEntity.ok(resp);
    }
}