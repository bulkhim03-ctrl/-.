package com.itproger.blog.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1) // Самый высокий приоритет
public class SwaggerFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();
        
        // Пропускаем все Swagger запросы без всяких проверок
        if (uri.contains("/swagger") || 
            uri.contains("/v3/api-docs") || 
            uri.contains("/swagger-resources") ||
            uri.contains("/webjars")) {
            try {
                chain.doFilter(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        
        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}