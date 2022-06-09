package com.babel.repositories;

import com.babel.entities.Author;
import com.babel.entities.AuthorItemRelationship;
import com.babel.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorItemRelationshipRepo extends JpaRepository<AuthorItemRelationship, Integer> {
    Optional<AuthorItemRelationship> findByAuthorAndAndItem(Author author, Item item);

    List<AuthorItemRelationship> findAllByItem(Item item);

    List<AuthorItemRelationship> findAllByAuthor(Author author);

    void deleteAuthorItemRelationshipByItem(Item item);


}
