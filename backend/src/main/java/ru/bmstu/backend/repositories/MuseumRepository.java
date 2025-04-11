package ru.bmstu.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bmstu.backend.models.Museum;

public interface MuseumRepository extends JpaRepository<Museum, Integer> {
}
