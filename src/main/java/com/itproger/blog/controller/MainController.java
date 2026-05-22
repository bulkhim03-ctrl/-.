package com.itproger.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Главная страница сайта, создатель Хисамиев Булат");
        return "home";
    }
    @GetMapping("/login-page")
public String loginPage() {
    return "login";
}

@GetMapping("/register")
public String registerPage() {
    return "register";
}
}