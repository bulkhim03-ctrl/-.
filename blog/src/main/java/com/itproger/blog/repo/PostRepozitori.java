package com.itproger.blog.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itproger.blog.models.Post;

public interface PostRepozitori extends JpaRepository<Post, Long> {
    // Метод для поиска постов по автору
    List<Post> findByAuthor(String author);
}