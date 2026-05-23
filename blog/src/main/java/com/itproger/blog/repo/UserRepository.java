package com.itproger.blog.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itproger.blog.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    
    // ЭТОТ МЕТОД УЖЕ ЕСТЬ, ПРОВЕРЬ! Если нет - добавь:
    // Optional<User> findByUsername(String username);
}