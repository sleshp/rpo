package ru.bmstu.backend.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.bmstu.backend.models.User;
import ru.bmstu.backend.repositories.UserRepository;
import ru.bmstu.backend.tools.Utils;
import ru.bmstu.backend.tools.utilModels.LoginRequest;
import ru.bmstu.backend.tools.utilModels.LoginResponse;
import ru.bmstu.backend.tools.utilModels.LoginStatus;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getLogin().isEmpty() || loginRequest.getPassword().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid login or password");
        } else {
            Optional<User> optionalUser = userRepository.findByLogin(loginRequest.getLogin());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                String hashedPassword = user.getPassword();
                String salt = user.getSalt();
                String computedHash = Utils.computeHash(loginRequest.getPassword(), salt);
                System.out.println(computedHash);
                if (hashedPassword.equalsIgnoreCase(computedHash)) {
                    String token = UUID.randomUUID().toString();
                    user.setToken(token);
                    user.setActivity(LocalDateTime.now());
                    User savedUser = userRepository.saveAndFlush(user);
                    return ResponseEntity.ok(new LoginResponse(LoginStatus.SUCCESS, savedUser));
                }
            }
        }
        return new ResponseEntity<>(new LoginResponse(LoginStatus.FAILURE, null), HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && !token.isEmpty()) {
            token = StringUtils.removeStart(token, "Bearer").trim();
            Optional<User> optionalUser = userRepository.findByToken(token);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setToken(null);
                userRepository.saveAndFlush(user);
                return ResponseEntity.ok("Logged out successfully");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
