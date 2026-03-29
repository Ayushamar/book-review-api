package com.ayush.bookreview.review.controller;

import com.ayush.bookreview.review.dto.ReviewRequest;
import com.ayush.bookreview.review.dto.ReviewResponse;
import com.ayush.bookreview.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Reviews", description = "Review APIs with business rules (1 review per user per book)")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    // ✅ Add Review
    @Operation(summary = "Add a review to a book (One review per user per book)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "User already reviewed this book"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/books/{bookId}/reviews")
    public ResponseEntity<ReviewResponse> addReview(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable Long bookId,

            @Valid @RequestBody ReviewRequest request,

            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        ReviewResponse response =
                reviewService.addReview(bookId, userEmail, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ✅ Get Reviews
    @Operation(summary = "Get all reviews for a specific book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<List<ReviewResponse>> getReviews(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable Long bookId
    ) {
        return ResponseEntity.ok(
                reviewService.getReviewsByBook(bookId)
        );
    }

    // ✅ Update Review
    @Operation(summary = "Update your own review (User must be owner)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "403", description = "Not allowed to update this review"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @Parameter(description = "Review ID", example = "1")
            @PathVariable Long reviewId,

            @Valid @RequestBody ReviewRequest request,

            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        return ResponseEntity.ok(
                reviewService.updateReview(reviewId, userEmail, request)
        );
    }

    // ✅ Delete Review
    @Operation(summary = "Delete your own review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Not allowed to delete this review"),
            @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "Review ID", example = "1")
            @PathVariable Long reviewId,

            Authentication authentication
    ) {
        String userEmail = authentication.getName();

        reviewService.deleteReview(reviewId, userEmail);

        return ResponseEntity.noContent().build();
    }
}