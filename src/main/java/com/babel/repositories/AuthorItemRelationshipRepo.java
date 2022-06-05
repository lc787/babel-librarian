package com.babel.repositories;

import com.babel.entities.AuthorItemRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorItemRelationshipRepo extends JpaRepository<AuthorItemRelationship, Integer> {
}
