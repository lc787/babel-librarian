package com.babel.repositories;

import com.babel.entities.Book;
import com.babel.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepo extends JpaRepository<Book, Integer> {
    Optional<Book> findByItem(Item item);
}
