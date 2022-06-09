package com.babel.repositories;

import com.babel.entities.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemTypeRepo extends JpaRepository<ItemType, Integer> {
    Optional<ItemType> findByType(String type);
}
