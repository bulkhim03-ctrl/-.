package com.itproger.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.itproger.blog.models.Post;
import com.itproger.blog.repo.PostRepozitori;

@Controller
public class BlogController {

    @Autowired
    private PostRepozitori postRepozitori;

    @GetMapping("/blog")
    public String blogMain (Model model){
        Iterable<Post> posts=postRepozitori.findAll();
        return "blogmain";
    }
    
}