# Book Review API System

A production-ready backend application built using Spring Boot, designed to demonstrate real-world backend engineering practices such as authentication, caching, pagination, and business rule enforcement.

---

## Tech Stack

- Java 17  
- Spring Boot  
- Spring Security (JWT)  
- Spring Data JPA (Hibernate)  
- PostgreSQL  
- Redis (Caching)  
- Maven  
- Swagger (OpenAPI)  

---

## Features

### Authentication & Authorization

- User registration and login  
- JWT-based authentication  
- Role-based access control (USER, ADMIN)

---

### Book Management

- Add book (ADMIN only)  
- Get all books with pagination and sorting  
- Search books by title and author  
- Get book by ID  
- Get top-rated books  

---

### Review System

- Add review  
- Update review  
- Delete review  
- Get all reviews for a book  

---

## Business Rule

A user can review a book only once.

This constraint is enforced at both:
- Database level (unique constraint)  
- Service level (validation logic)  

---

## Rating System

- Rating range: 1 to 5  
- Average rating is automatically calculated per book  
- Updated whenever a review is added, updated, or deleted  

---

## Caching (Redis)

Caching is applied to improve performance for read-heavy operations:

- Book list (paginated results)  
- Search results  
- Top-rated books  

Cache is invalidated when:
- A new book is added  
- Reviews are modified (affecting ratings)  

---

## API Endpoints

### Auth

- POST `/api/auth/register`  
- POST `/api/auth/login`  

---

### Books

- GET `/api/books` (pagination + search)  
- GET `/api/books/{id}`  
- POST `/api/books` (ADMIN only)  
- GET `/api/books/top-rated`  

---

### Reviews

- POST `/api/books/{id}/reviews`  
- GET `/api/books/{id}/reviews`  
- PUT `/api/reviews/{id}`  
- DELETE `/api/reviews/{id}`  

---

## API Documentation

Swagger UI is available at:

http://localhost:8080/swagger-ui/index.html

---

## Authentication in Swagger

1. Login using `/api/auth/login`  
2. Copy the JWT token  
3. Click "Authorize" in Swagger  
4. Enter: Bearer <token>  

---

## Project Structure


book-review-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── ayush/
│   │   │           └── bookreview/
│   │   │               ├── BookreviewApplication.java
│   │   │               ├── auth/
│   │   │               │   ├── controller/
│   │   │               │   ├── service/
│   │   │               │   └── dto/
│   │   │               ├── book/
│   │   │               │   ├── controller/
│   │   │               │   ├── service/
│   │   │               │   ├── dto/
│   │   │               │   └── repository/
│   │   │               ├── review/
│   │   │               │   ├── controller/
│   │   │               │   ├── service/
│   │   │               │   ├── dto/
│   │   │               │   └── repository/
│   │   │               ├── user/
│   │   │               │   ├── service/
│   │   │               │   ├── dto/
│   │   │               │   └── repository/
│   │   │               ├── entity/
│   │   │               │   ├── User.java
│   │   │               │   ├── Book.java
│   │   │               │   └── Review.java
│   │   │               ├── enums/
│   │   │               │   └── Role.java
│   │   │               ├── common/
│   │   │               │   └── config/
│   │   │               │       └── RedisConfig.java
│   │   │               ├── config/
│   │   │               │   └── SwaggerConfig.java
│   │   │               ├── security/
│   │   │               │   ├── JwtFilter.java
│   │   │               │   ├── JwtService.java
│   │   │               │   ├── SecurityConfig.java
│   │   │               │   └── CustomUserDetailsService.java
│   │   │               ├── exception/
│   │   │               │   ├── GlobalExceptionHandler.java
│   │   │               │   ├── ResourceNotFoundException.java
│   │   │               │   └── DuplicateResourceException.java
│   │   │               └── util/
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   └── data.sql
│   │   └── test/
│   │       └── java/
│   │           └── com/
│   │               └── ayush/
│   │                   └── bookreview/
│   │                       └── BookreviewApplicationTests.java
│   └── test/
├── pom.xml
├── README.md
└── .gitignore


## Architecture

The application follows a layered architecture:

Controller → Service → Repository → Database

- DTOs are used for request/response handling  
- Global exception handling is implemented  
- Input validation is handled using annotations  

---

## Design Considerations

- Stateless authentication using JWT  
- Redis caching for performance optimization  
- Business rules enforced at multiple layers  
- Pagination for scalability  
- Clean separation of concerns  

---

## Future Improvements

- Docker-based deployment  