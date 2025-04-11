package ru.bmstu.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bmstu.backend.models.Artist;

public interface ArtistRepository extends JpaRepository<Artist, Integer> {}