package com.itproger.blog.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    
    @Value("${jwt.access_cookie_name}")
    private String accessTokenCookieName;
    
    @Value("${jwt.refresh_cookie_name}")
    private String refreshTokenCookieName;
    
    public HttpCookie createAccessTokenCookie(String accessToken, long duration) {
        return ResponseCookie.from(accessTokenCookieName, accessToken)
                .maxAge(duration)
                .httpOnly(true)
                .secure(false)  // Для localhost false, для HTTPS true
                .path("/")
                .sameSite("Lax")
                .build();
    }
    
    public HttpCookie createRefreshTokenCookie(String refreshToken, long duration) {
        return ResponseCookie.from(refreshTokenCookieName, refreshToken)
                .maxAge(duration)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .build();
    }
    
    public HttpCookie deleteAccessTokenCookie() {
        return ResponseCookie.from(accessTokenCookieName, "")
                .maxAge(0)
                .httpOnly(true)
                .path("/")
                .build();
    }
    
    public HttpCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(refreshTokenCookieName, "")
                .maxAge(0)
                .httpOnly(true)
                .path("/")
                .build();
    }
}