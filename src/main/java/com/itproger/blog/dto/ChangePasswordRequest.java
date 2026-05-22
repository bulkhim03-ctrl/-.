package com.itproger.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank(message = "Текущий пароль не может быть пустым")
    String currentPassword,
    
    @NotBlank(message = "Новый пароль не может быть пустым")
    @Size(min = 4, message = "Новый пароль должен содержать минимум 4 символа")
    String newPassword,
    
    @NotBlank(message = "Подтверждение пароля не может быть пустым")
    String confirmPassword
) {}