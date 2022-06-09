package com.babel.repositories;

import com.babel.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepo extends JpaRepository<Genre, Integer> {
    Optional<Genre> findByName(String name);
}
