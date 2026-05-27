package com.itproger.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.itproger.blog.services.AuthService;

@Controller
public class MainController {

    @Autowired
    private AuthService authService;
    
    private String getCurrentUsername() {
        try {
            return authService.getUserLoggedInfo().username();
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Главная страница сайта, создатель Хисамиев Булат");
        model.addAttribute("currentUser", getCurrentUsername());
        return "home";
    }
    
    @GetMapping("/login-page")
    public String loginPage(Model model) {
        model.addAttribute("currentUser", getCurrentUsername());
        return "login";
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("currentUser", getCurrentUsername());
        return "register";
    }
}