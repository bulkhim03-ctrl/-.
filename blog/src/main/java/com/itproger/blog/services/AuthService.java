package com.itproger.blog.services;

import org.springframework.http.ResponseEntity;

import com.itproger.blog.dto.LoginRequest;
import com.itproger.blog.dto.LoginResponse;
import com.itproger.blog.dto.UserLoggedDto;

public interface AuthService {
    ResponseEntity<LoginResponse> login(LoginRequest loginRequest, String accessToken, String refreshToken);
    ResponseEntity<LoginResponse> refresh(String refreshToken);
    ResponseEntity<LoginResponse> logout(String accessToken, String refreshToken);
    UserLoggedDto getUserLoggedInfo();
}