package com.SE.final_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.AppNotification;
import com.SE.final_project.model.User;

public interface NotificationRepository extends JpaRepository<AppNotification, Long> {

    List<AppNotification> findTop15ByUserOrderByCreatedAtDesc(User user);

    long countByUserAndReadFalse(User user);
}
