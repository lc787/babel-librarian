package com.babel.services;

import com.babel.entities.Author;
import com.babel.entities.AuthorItemRelationship;
import com.babel.entities.Item;
import com.babel.exceptions.GenericDatabaseException;
import com.babel.exceptions.IllegalAuthorNameException;
import com.babel.exceptions.NotAnItemException;
import com.babel.repositories.AuthorItemRelationshipRepo;
import com.babel.repositories.AuthorRepo;
import com.babel.repositories.ItemRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthorItemRelationshipService {
    private final AuthorRepo authorRepo;
    private final ItemRepo itemRepo;
    private final AuthorItemRelationshipRepo authorItemRelationshipRepo;

    private final AuthorService authorService;

    public AuthorItemRelationshipService(AuthorRepo authorRepo, ItemRepo itemRepo, AuthorItemRelationshipRepo authorItemRelationshipRepo, AuthorService authorService) {
        this.authorRepo = authorRepo;
        this.itemRepo = itemRepo;
        this.authorItemRelationshipRepo = authorItemRelationshipRepo;
        this.authorService = authorService;
    }

    public List<Author> getAuthorsOfItem(Item item) {
        List<AuthorItemRelationship> authorItemRelationshipList = authorItemRelationshipRepo.findAllByItem(item);
        List<Author> authorList = new ArrayList<>();
        authorItemRelationshipList.forEach(authorItemRelationship -> authorList.add(authorItemRelationship.getAuthor()));
        return authorList;
    }

    public List<Item> getItemsOfAuthor(Author author) {

        List<AuthorItemRelationship> authorItemRelationshipList = authorItemRelationshipRepo.findAllByAuthor(author);
        List<Item> itemList = new ArrayList<>();
        authorItemRelationshipList.forEach(authorItemRelationship -> itemList.add(authorItemRelationship.getItem()));
        return itemList;
    }

    public void addAuthorItemRelationship(Item item, Author author) throws NotAnItemException, IllegalAuthorNameException {
        if (itemRepo.findById(item.getId()).isEmpty()) throw new NotAnItemException("No item found in db");
        //If the author doesn't exist we create it
        author = authorService.addAuthor(author.getName());
        authorItemRelationshipRepo.save(new AuthorItemRelationship(author, item));
    }

    public AuthorItemRelationship getAuthorItemRelationship(Item item, Author author) throws IllegalAuthorNameException, GenericDatabaseException {
        authorService.checkAuthorName(author.getName());
        Optional<AuthorItemRelationship> authorItemRelationshipOptional = authorItemRelationshipRepo.findByAuthorAndAndItem(author, item);
        if (authorItemRelationshipOptional.isEmpty()) throw new GenericDatabaseException("No such relationship found");
        return authorItemRelationshipOptional.get();
    }

    public void deleteAuthorItemRelationship(Item item) {
        authorItemRelationshipRepo.deleteAuthorItemRelationshipByItem(item);
    }
}
