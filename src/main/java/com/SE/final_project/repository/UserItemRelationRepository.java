package com.SE.final_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.Item;
import com.SE.final_project.model.RelationType;
import com.SE.final_project.model.User;
import com.SE.final_project.model.UserItemRelation;

public interface UserItemRelationRepository extends JpaRepository<UserItemRelation, Long> {

    long countByRelationType(RelationType relationType);

    boolean existsByUserAndItemAndRelationType(User user, Item item, RelationType relationType);

    List<UserItemRelation> findByUserAndRelationType(User user, RelationType relationType);

    List<UserItemRelation> findByItemAndRelationType(Item item, RelationType relationType);
}