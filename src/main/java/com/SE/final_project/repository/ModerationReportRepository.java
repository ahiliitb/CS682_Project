package com.SE.final_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.ModerationReport;
import com.SE.final_project.model.ModerationStatus;

public interface ModerationReportRepository extends JpaRepository<ModerationReport, Long> {

    List<ModerationReport> findTop100ByStatusOrderByCreatedAtDesc(ModerationStatus status);

    List<ModerationReport> findTop100ByOrderByCreatedAtDesc();
}
