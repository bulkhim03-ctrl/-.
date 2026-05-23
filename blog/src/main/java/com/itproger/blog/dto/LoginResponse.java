package com.itproger.blog.dto;

public record LoginResponse(
    boolean isLogged,
    String roles
) {
}