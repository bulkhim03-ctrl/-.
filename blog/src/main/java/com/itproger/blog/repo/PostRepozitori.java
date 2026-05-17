package com.itproger.blog.repo;

import com.itproger.blog.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepozitori extends JpaRepository<Post, Long> {
}