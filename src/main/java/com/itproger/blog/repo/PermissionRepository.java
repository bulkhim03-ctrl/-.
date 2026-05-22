package com.itproger.blog.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itproger.blog.models.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByResourceAndOperation(String resource, String operation);
}