package com.babel.services;

import com.babel.entities.Author;
import com.babel.exceptions.IllegalAuthorNameException;
import com.babel.exceptions.NotAnAuthorException;
import com.babel.repositories.AuthorRepo;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorService {
    private final AuthorRepo authorRepo;

    public AuthorService(AuthorRepo authorRepo) {
        this.authorRepo = authorRepo;
    }

    public Author getAuthor(int id) throws NotAnAuthorException {
        Author author = authorRepo.findById(id).orElse(null);
        if (author == null) throw new NotAnAuthorException("No author with such id registered");
        return author;
    }

    public Author getAuthor(String name) throws NotAnAuthorException, IllegalAuthorNameException {
        checkAuthorName(name);
        Author author = authorRepo.findByName(name).orElse(null);
        if (author == null) throw new NotAnAuthorException("No author registered");
        return author;
    }

    public Author addAuthor(String name) throws IllegalAuthorNameException {
        checkAuthorName(name);
        Optional<Author> authorOptional = authorRepo.findByName(name);
        if (authorOptional.isEmpty()) {
            Author author = new Author(name);
            authorRepo.save(author);
            //TODO: Does the returned author have a valid id?
            return author;
        }
        return authorOptional.get();
    }

    /**
     * Author name check. More or less copy-pasted from password checker
     *
     * @param name
     * @throws IllegalAuthorNameException
     */
    public void checkAuthorName(String name) throws IllegalAuthorNameException {
        if (name == null)
            throw new IllegalAuthorNameException("Empty author field");
        if (name.length() < 2)
            throw new IllegalAuthorNameException("Author name is too short. Minimum allowed length is " + 2);
        if (name.length() > 64)
            throw new IllegalAuthorNameException("Author name is too long. Maximum allowed length is " + 64);
        if (!name.matches("[A-z .]+"))
            throw new IllegalAuthorNameException("Author name contains illegal characters. A name may only contain letters, dots and spaces");
    }

}
