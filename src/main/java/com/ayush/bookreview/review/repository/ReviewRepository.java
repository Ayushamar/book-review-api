package com.ayush.bookreview.review.repository;

import com.ayush.bookreview.entity.Book;
import com.ayush.bookreview.entity.Review;
import com.ayush.bookreview.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Check if a user has already reviewed a book (Business Rule)
    boolean existsByUserAndBook(User user, Book book);

    // Get all reviews for a specific book
    List<Review> findByBook(Book book);
}