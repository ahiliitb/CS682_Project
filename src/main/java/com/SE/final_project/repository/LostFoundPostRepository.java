package com.SE.final_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.LostFoundPost;

public interface LostFoundPostRepository extends JpaRepository<LostFoundPost, Long> {

    List<LostFoundPost> findAllByOrderByCreatedAtDesc();
}
