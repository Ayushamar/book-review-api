package com.ayush.bookreview.book.repository;

import com.ayush.bookreview.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    // 🔥 SEARCH + PAGINATION
    Page<Book> findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(
            String title,
            String author,
            Pageable pageable
    );

    // 🔥 TOP RATED BOOKS (VERY IMPORTANT)
    Page<Book> findAllByOrderByAverageRatingDesc(Pageable pageable);
}