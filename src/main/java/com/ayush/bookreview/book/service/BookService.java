package com.ayush.bookreview.book.service;

import com.ayush.bookreview.book.dto.BookRequest;
import com.ayush.bookreview.book.dto.BookResponse;
import com.ayush.bookreview.book.repository.BookRepository;
import com.ayush.bookreview.common.exception.ResourceNotFoundException;
import com.ayush.bookreview.entity.Book;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // 🔥 CREATE BOOK → CLEAR ALL RELATED CACHE
    @CacheEvict(value = {"books", "top_books"}, allEntries = true)
    public BookResponse createBook(BookRequest request) {

        System.out.println("🧹 CACHE CLEARED (BOOK CREATED)");

        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setAverageRating(0.0);

        Book saved = bookRepository.save(book);

        return mapToResponse(saved);
    }

    // 🔥 SEARCH + PAGINATION + CACHE
    @Cacheable(
        value = "books",
        key = "#title + '-' + #author + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort"
    )
    public Page<BookResponse> getBooks(String title, String author, Pageable pageable) {

        System.out.println("🔥 DB HIT: FETCH BOOKS");

        String safeTitle = (title == null) ? "" : title;
        String safeAuthor = (author == null) ? "" : author;

        return bookRepository
                .findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCase(
                        safeTitle,
                        safeAuthor,
                        pageable
                )
                .map(this::mapToResponse);
    }

    // 🔥 TOP RATED BOOKS (CACHE)
    @Cacheable(
        value = "top_books",
        key = "#pageable.pageNumber + '-' + #pageable.pageSize"
    )
    public Page<BookResponse> getTopRatedBooks(Pageable pageable) {

        System.out.println("🔥 DB HIT: TOP RATED BOOKS");

        return bookRepository
                .findAllByOrderByAverageRatingDesc(pageable)
                .map(this::mapToResponse);
    }

    // ✅ Get Book by ID
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        return mapToResponse(book);
    }

    // 🔥 Mapper
    private BookResponse mapToResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .averageRating(book.getAverageRating())
                .build();
    }
}