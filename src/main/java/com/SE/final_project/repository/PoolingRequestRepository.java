package com.SE.final_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.PoolingRequest;

public interface PoolingRequestRepository extends JpaRepository<PoolingRequest, Long> {

    List<PoolingRequest> findAllByOrderByCreatedAtDesc();
}
