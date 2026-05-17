package com.itproger.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itproger.blog.models.Post;
import com.itproger.blog.repo.PostRepozitori;

@Controller
public class BlogController {

    @Autowired
    private PostRepozitori postRepozitori;

    @GetMapping("/blog")
    public String blogMain (Model model){
        Iterable<Post> posts=postRepozitori.findAll();
        model.addAttribute("posts", posts);
        return "blogmain";
    }

    @GetMapping("/blog/add")
    public String blogAdd (Model model){
        return "blogadd";
    }

    @PostMapping("/blog/add")
    public String blogPostAdd (@RequestParam String title, @RequestParam String anons, @RequestParam String full_text, Model model){
        Post post=new Post(title,anons,full_text);
        postRepozitori.save(post);
        return "redirect:/blog";
    }
}