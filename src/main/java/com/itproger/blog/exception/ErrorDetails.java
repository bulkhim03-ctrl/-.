package com.itproger.blog.exception;

import java.time.LocalDateTime;

public record ErrorDetails(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String details
) {}