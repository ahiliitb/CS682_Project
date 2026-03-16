package com.SE.final_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}