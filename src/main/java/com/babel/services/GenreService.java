package com.babel.services;

import com.babel.entities.Genre;
import com.babel.exceptions.IllegalGenreNameException;
import com.babel.exceptions.NotAnGenreException;
import com.babel.repositories.GenreRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GenreService {
    private final GenreRepo genreRepo;

    public GenreService(GenreRepo genreRepo) {
        this.genreRepo = genreRepo;
    }

    public Genre getGenre(int id) throws NotAnGenreException {
        Genre genre = genreRepo.findById(id).orElse(null);
        if (genre == null) throw new NotAnGenreException("No genre with such id registered");
        return genre;
    }

    public Genre getGenre(String name) throws NotAnGenreException {
        try {
            checkGenreName(name);
        } catch (IllegalGenreNameException e) {
            throw new NotAnGenreException("No genre registered");
        }
        Genre genre = genreRepo.findByName(name).orElse(null);
        if (genre == null) throw new NotAnGenreException("No genre registered");
        return genre;
    }

    public Genre addGenre(String name) throws IllegalGenreNameException {
        checkGenreName(name);
        Optional<Genre> genreOptional = genreRepo.findByName(name);
        if (genreOptional.isEmpty()) {
            Genre genre = new Genre(name);
            genreRepo.save(genre);
            //TODO: Does the returned genre have a valid id?
            return genre;
        }
        return genreOptional.get();
    }

    /**
     * Genre name check. More or less copy-pasted from password checker
     *
     * @param name
     * @throws IllegalGenreNameException
     */
    private void checkGenreName(String name) throws IllegalGenreNameException {
        if (name == null)
            throw new IllegalGenreNameException("Empty genre field");
        if (name.length() < 2)
            throw new IllegalGenreNameException("Genre is too short. Minimum allowed length is " + 2);
        if (name.length() > 32)
            throw new IllegalGenreNameException("Genre is too long. Maximum allowed length is " + 32);
        if (!name.matches("[A-z0-9 ]+"))
            throw new IllegalGenreNameException("Genre contains illegal characters. A genre may only contain alphanumerics and spaces");
    }

}
