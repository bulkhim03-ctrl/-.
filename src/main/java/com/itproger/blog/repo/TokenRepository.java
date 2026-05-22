package com.itproger.blog.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.itproger.blog.models.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
}