package com.SE.final_project.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.SsoAccessCode;

public interface SsoAccessCodeRepository extends JpaRepository<SsoAccessCode, Long> {

    SsoAccessCode findByCode(String code);

    List<SsoAccessCode> findTop50ByUsedAtIsNullAndExpiresAtAfterOrderByCreatedAtDesc(LocalDateTime now);

    List<SsoAccessCode> findTop100ByOrderByCreatedAtDesc();
}
