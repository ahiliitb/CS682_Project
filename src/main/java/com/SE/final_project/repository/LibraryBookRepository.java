package com.SE.final_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.LibraryBook;
import com.SE.final_project.model.LibraryBookStatus;
import com.SE.final_project.model.User;

public interface LibraryBookRepository extends JpaRepository<LibraryBook, Long> {

    List<LibraryBook> findByStatusOrderByCreatedAtDesc(LibraryBookStatus status);

    List<LibraryBook> findByOwnerOrderByCreatedAtDesc(User owner);

    List<LibraryBook> findByBorrowerOrderByCreatedAtDesc(User borrower);

    List<LibraryBook> findAllByOrderByCreatedAtDesc();
}
