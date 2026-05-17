package com.itproger.blog.controller;

import com.itproger.blog.models.User;
import com.itproger.blog.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;

@Controller
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }
    
    @GetMapping("/login-page")
    public String loginPage() {
        return "login";
    }
    
    @PostMapping("/api/register")
    @ResponseBody
    public String register(@RequestParam String username, 
                           @RequestParam String password) {
        
        if (username == null || username.trim().isEmpty()) {
            return "Имя пользователя не может быть пустым";
        }
        
        if (password == null || password.trim().isEmpty()) {
            return "Пароль не может быть пустым";
        }
        
        if (username.length() < 3) {
            return "Имя пользователя должно содержать минимум 3 символа";
        }
        
        if (password.length() < 4) {
            return "Пароль должен содержать минимум 4 символа";
        }
        
        if (userRepository.existsByUsername(username)) {
            return "Пользователь с таким именем уже существует";
        }
        
        User user = new User(
            username,
            passwordEncoder.encode(password),
            Set.of("ROLE_USER")
        );
        
        userRepository.save(user);
        return "Регистрация успешно завершена";
    }
}