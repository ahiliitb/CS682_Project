package com.SE.final_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.User;

//Add a verification token repository

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByEmail(String email);

    User findByVerificationToken(String token);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}