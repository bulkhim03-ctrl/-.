package com.itproger.blog.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.itproger.blog.dto.LoginRequest;
import com.itproger.blog.dto.LoginResponse;
import com.itproger.blog.dto.UserDto;
import com.itproger.blog.dto.UserLoggedDto;
import com.itproger.blog.services.UserService;
import com.itproger.blog.services.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;

@Tag(name = "Authentication", description = "API для аутентификации и управления сессиями")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthServiceImpl authService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Логин пользователя")
    @ApiResponse(responseCode = "200", description = "Успешная аутентификация")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @CookieValue(name = "access_token", required = false) String accessToken,
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest, accessToken, refreshToken);
    }
    
    @Operation(summary = "Регистрация пользователя")
    @ApiResponse(responseCode = "200", description = "Пользователь создан")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody LoginRequest loginRequest) {
        System.out.println("📝 Регистрация: " + loginRequest.username());
        
        if (loginRequest.password().length() < 4) {
            return ResponseEntity.badRequest().body("Пароль должен содержать минимум 4 символа");
        }
        
        // Проверка существования пользователя
        try {
            userService.getUser(loginRequest.username());
            return ResponseEntity.badRequest().body("Пользователь уже существует");
        } catch (Exception e) {
            // Пользователь не найден - можно создавать
        }
        
        // Кодируем пароль
        String encodedPassword = passwordEncoder.encode(loginRequest.password());
        System.out.println("🔐 Закодированный пароль: " + encodedPassword);
        
        // Создаём нового пользователя с ролью USER
        UserDto newUser = new UserDto(
            null,
            loginRequest.username(),
            encodedPassword,
            "USER",
            null
        );
        
        userService.create(newUser);
        System.out.println("✅ Пользователь создан: " + loginRequest.username());
        
        return ResponseEntity.ok("Регистрация успешна");
    }

    @Operation(summary = "Обновление токена")
    @ApiResponse(responseCode = "200", description = "Токен успешно обновлен")
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }
        return authService.refresh(refreshToken);
    }

    @Operation(summary = "Выход из системы")
    @ApiResponse(responseCode = "200", description = "Сессия завершена")
    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout(
            @CookieValue(name = "access_token", required = false) String accessToken,
            @CookieValue(name = "refresh_token", required = false) String refreshToken) {
        return authService.logout(accessToken, refreshToken);
    }

    @Operation(summary = "Информация о пользователе")
    @ApiResponse(responseCode = "200", description = "Данные пользователя")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public ResponseEntity<UserLoggedDto> userLoggedInfo() {
        return ResponseEntity.ok(authService.getUserLoggedInfo());
    }
}