package com.SE.final_project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    long countByActiveTrue();

    List<Item> findByActiveTrueOrderByCreatedAtDesc();

    Optional<Item> findByIdAndActiveTrue(Long id);
}