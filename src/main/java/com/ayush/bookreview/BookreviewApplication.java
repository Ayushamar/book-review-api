package com.ayush.bookreview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching // 🔥 Enables Redis caching
public class BookreviewApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookreviewApplication.class, args);
    }
}