package com.SE.final_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {

    long countByActiveTrue();
}