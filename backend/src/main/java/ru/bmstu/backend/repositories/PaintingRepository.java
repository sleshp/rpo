package ru.bmstu.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bmstu.backend.models.Painting;

public interface PaintingRepository extends JpaRepository<Painting, Integer> {
}