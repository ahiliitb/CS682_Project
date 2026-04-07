package com.SE.final_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByOrderByCreatedAtDesc();
}