package com.itproger.blog.services.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.itproger.blog.dto.LoginRequest;
import com.itproger.blog.dto.LoginResponse;
import com.itproger.blog.dto.UserLoggedDto;
import com.itproger.blog.exception.AppException;
import com.itproger.blog.jwt.JwtTokenProvider;
import com.itproger.blog.mapper.UserMapper;
import com.itproger.blog.models.Token;
import com.itproger.blog.models.User;
import com.itproger.blog.repo.TokenRepository;
import com.itproger.blog.repo.UserRepository;
import com.itproger.blog.services.AuthService;
import com.itproger.blog.util.CookieUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    @Value("${jwt.access_token_duration_minute}")
    private long accessTokenDurationMinute;
    
    @Value("${jwt.access_token_duration_second}")
    private long accessTokenDurationSecond;
    
    @Value("${jwt.refresh_token_duration_day}")
    private long refreshTokenDurationDay;
    
    @Value("${jwt.refresh_token_duration_second}")
    private long refreshTokenDurationSecond;
    
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtTokenProvider tokenProvider;
    private final CookieUtil cookieUtil;
    private final AuthenticationManager authenticationManager;
    
    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest loginRequest, String accessToken, String refreshToken) {
        System.out.println("🔐 Попытка входа: " + loginRequest.username());
        
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password())
            );
            
            String username = loginRequest.username();
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));
            
            System.out.println("✅ Аутентификация успешна для: " + username);
            
            // Удаляем старые токены пользователя
            if (user.getTokens() != null) {
                tokenRepository.deleteAll(user.getTokens());
            }
            
            // Генерируем токены
            Token newAccessToken = tokenProvider.generateAccessToken(
                Map.of("role", user.getRole() != null ? user.getRole().getAuthority() : "USER"),
                accessTokenDurationMinute, ChronoUnit.MINUTES, user
            );
            
            Token newRefreshToken = tokenProvider.generateRefreshToken(
                refreshTokenDurationDay, ChronoUnit.DAYS, user
            );
            
            newAccessToken.setUser(user);
            newRefreshToken.setUser(user);
            
            // Сохраняем токены
            tokenRepository.save(newAccessToken);
            tokenRepository.save(newRefreshToken);
            
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.add(HttpHeaders.SET_COOKIE, 
                cookieUtil.createAccessTokenCookie(newAccessToken.getValue(), accessTokenDurationSecond).toString());
            responseHeaders.add(HttpHeaders.SET_COOKIE, 
                cookieUtil.createRefreshTokenCookie(newRefreshToken.getValue(), refreshTokenDurationSecond).toString());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            LoginResponse loginResponse = new LoginResponse(true, user.getRole() != null ? user.getRole().getName() : "USER");
            return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
            
        } catch (Exception e) {
            System.out.println("❌ Ошибка аутентификации: " + e.getMessage());
            e.printStackTrace();
            throw new AppException(HttpStatus.UNAUTHORIZED, "Неверные учетные данные пользователя");
        }
    }
    
    @Override
    public ResponseEntity<LoginResponse> refresh(String refreshToken) {
        boolean refreshTokenValid = tokenProvider.validateToken(refreshToken);
        if (!refreshTokenValid)
            throw new AppException(HttpStatus.BAD_REQUEST, "Refresh token is invalid");

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        Token newAccessToken = tokenProvider.generateAccessToken(
            Map.of("role", user.getRole().getAuthority()),
            accessTokenDurationMinute, ChronoUnit.MINUTES, user
        );

        tokenRepository.save(newAccessToken);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, 
            cookieUtil.createAccessTokenCookie(newAccessToken.getValue(), accessTokenDurationSecond).toString());

        LoginResponse loginResponse = new LoginResponse(true, user.getRole().getName());
        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
    }
    
    @Override
    public ResponseEntity<LoginResponse> logout(String accessToken, String refreshToken) {
        SecurityContextHolder.clearContext();

        if (accessToken != null) {
            String username = tokenProvider.getUsernameFromToken(accessToken);
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null && user.getTokens() != null) {
                tokenRepository.deleteAll(user.getTokens());
            }
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.deleteAccessTokenCookie().toString());
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.deleteRefreshTokenCookie().toString());

        LoginResponse loginResponse = new LoginResponse(false, null);
        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);
    }
    
    @Override
    public UserLoggedDto getUserLoggedInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken)
            throw new AppException(HttpStatus.UNAUTHORIZED, "No user authenticated");

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND, "User not found"));

        return UserMapper.userToUserLoggedDto(user);
    }
}