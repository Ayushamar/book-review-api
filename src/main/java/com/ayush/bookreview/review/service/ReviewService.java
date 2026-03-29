package com.ayush.bookreview.review.service;

import com.ayush.bookreview.book.repository.BookRepository;
import com.ayush.bookreview.common.exception.DuplicateResourceException;
import com.ayush.bookreview.common.exception.ResourceNotFoundException;
import com.ayush.bookreview.entity.Book;
import com.ayush.bookreview.entity.Review;
import com.ayush.bookreview.entity.User;
import com.ayush.bookreview.review.dto.ReviewRequest;
import com.ayush.bookreview.review.dto.ReviewResponse;
import com.ayush.bookreview.review.repository.ReviewRepository;
import com.ayush.bookreview.user.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         BookRepository bookRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    // ✅ Add Review (JWT-based)
    @CacheEvict(value = {"books", "book"}, allEntries = true) // 🔥 IMPORTANT
    public ReviewResponse addReview(Long bookId, String userEmail, ReviewRequest request) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        // 🔥 Business rule: one review per user per book
        if (reviewRepository.existsByUserAndBook(user, book)) {
            throw new DuplicateResourceException("User has already reviewed this book");
        }

        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);

        updateAverageRating(book);

        return mapToResponse(saved);
    }

    // ✅ Update Review (ownership enforced)
    @CacheEvict(value = {"books", "book"}, allEntries = true) // 🔥 IMPORTANT
    public ReviewResponse updateReview(Long reviewId, String userEmail, ReviewRequest request) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // 🔒 Ownership check
        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new DuplicateResourceException("You are not allowed to update this review");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review updated = reviewRepository.save(review);

        updateAverageRating(review.getBook());

        return mapToResponse(updated);
    }

    // ✅ Delete Review
    @CacheEvict(value = {"books", "book"}, allEntries = true) // 🔥 IMPORTANT
    public void deleteReview(Long reviewId, String userEmail) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // 🔒 Ownership check
        if (!review.getUser().getEmail().equals(userEmail)) {
            throw new DuplicateResourceException("You are not allowed to delete this review");
        }

        Book book = review.getBook();

        reviewRepository.delete(review);

        updateAverageRating(book);
    }

    // ✅ Get Reviews by Book
    public List<ReviewResponse> getReviewsByBook(Long bookId) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        return reviewRepository.findByBook(book)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 🔥 Average Rating Logic (CORE BUSINESS LOGIC)
    private void updateAverageRating(Book book) {

        List<Review> reviews = reviewRepository.findByBook(book);

        double avg = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        book.setAverageRating(avg);

        bookRepository.save(book);
    }

    // 🔥 DTO Mapper (Clean Architecture)
    private ReviewResponse mapToResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getUser().getEmail(),
                review.getBook().getId()
        );
    }
}