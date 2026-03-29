package com.ayush.bookreview.book.controller;

import com.ayush.bookreview.book.dto.BookRequest;
import com.ayush.bookreview.book.dto.BookResponse;
import com.ayush.bookreview.book.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Book management APIs (Search, Pagination, Top Rated)")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // ✅ Create Book (ADMIN)
    @Operation(summary = "Create a new book (ADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book created successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied (Not ADMIN)")
    })
    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @Valid @RequestBody BookRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookService.createBook(request));
    }

    // 🔥 SEARCH + PAGINATION
    @Operation(summary = "Get all books with search, pagination and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Books fetched successfully")
    })
    @GetMapping
    public ResponseEntity<Page<BookResponse>> getBooks(
            @Parameter(description = "Search by title (optional)")
            @RequestParam(required = false) String title,

            @Parameter(description = "Search by author (optional)")
            @RequestParam(required = false) String author,

            @Parameter(description = "Pagination and sorting details")
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                bookService.getBooks(title, author, pageable)
        );
    }

    // 🔥 TOP RATED BOOKS
    @Operation(summary = "Get top rated books (sorted by rating)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top rated books fetched")
    })
    @GetMapping("/top-rated")
    public ResponseEntity<Page<BookResponse>> getTopRatedBooks(
            @Parameter(description = "Pagination details")
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                bookService.getTopRatedBooks(pageable)
        );
    }

    // ✅ Get by ID
    @Operation(summary = "Get book details by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<BookResponse> getBookById(
            @Parameter(description = "Book ID", example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }
}