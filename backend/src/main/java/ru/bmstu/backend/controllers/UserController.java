package ru.bmstu.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.bmstu.backend.models.Museum;
import ru.bmstu.backend.models.User;
import ru.bmstu.backend.repositories.MuseumRepository;
import ru.bmstu.backend.repositories.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MuseumRepository museumRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User savedUser = userRepository.save(user);
            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User user) {
        User userObj;
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            userObj = optionalUser.get();
            userObj.setLogin(user.getLogin());
            userObj.setEmail(user.getEmail());
            userRepository.save(userObj);
            return ResponseEntity.ok(userObj);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable int id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
            return ResponseEntity.ok(optionalUser.get());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }

    @PostMapping("/{id}/museums")
    public ResponseEntity<User> addMuseums(@PathVariable int id, @RequestBody Set<Museum> museums) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            for (Museum museum : museums) {
                Optional<Museum> optionalMuseum = museumRepository.findById(museum.getId());
                optionalMuseum.ifPresent(user::addMuseum);
                if (optionalMuseum.isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Museum not found");
                }
            }
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
    @DeleteMapping("/{id}/museums")
    public ResponseEntity<User> removeMuseums(@PathVariable int id, @RequestBody Set<Museum> museums) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            for (Museum museum : museums) {
                Optional<Museum> managedMuseum = museumRepository.findById(museum.getId());
                managedMuseum.ifPresent(user::removeMuseum);
            }
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
