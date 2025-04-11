package ru.bmstu.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bmstu.backend.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByToken(String token);
    Optional<User> findByLogin(String login);
}
