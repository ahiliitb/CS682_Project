package com.SE.final_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.RelationType;
import com.SE.final_project.model.UserItemRelation;

public interface UserItemRelationRepository extends JpaRepository<UserItemRelation, Long> {

    long countByRelationType(RelationType relationType);
}