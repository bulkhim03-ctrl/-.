package com.itproger.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.itproger.blog.models.Post;
import com.itproger.blog.repo.PostRepozitori;
import java.util.Optional;

@Controller
public class BlogController {

    @Autowired
    private PostRepozitori postRepozitori;
    
    // Получить имя текущего авторизованного пользователя
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return auth.getName();
        }
        return null;
    }

    @GetMapping("/blog")
    public String blogMain(Model model) {
        Iterable<Post> posts = postRepozitori.findAll();
        model.addAttribute("posts", posts);
        model.addAttribute("currentUser", getCurrentUsername());
        return "blogmain";
    }
    
    // Детальная страница статьи
    @GetMapping("/blog/{id}")
    public String blogDetail(@PathVariable(value = "id") long id, Model model) {
        Optional<Post> post = postRepozitori.findById(id);
        if (post.isEmpty()) {
            return "redirect:/blog";
        }
        
        // Увеличиваем просмотры
        Post article = post.get();
        article.setViews(article.getViews() + 1);
        postRepozitori.save(article);
        
        model.addAttribute("post", article);
        model.addAttribute("currentUser", getCurrentUsername());
        return "blogdetail";
    }

    @GetMapping("/blog/add")
    public String blogAdd(Model model) {
        return "blogadd";
    }

    @PostMapping("/blog/add")
    public String blogPostAdd(@RequestParam String title, 
                              @RequestParam String anons, 
                              @RequestParam String full_text) {
        String author = getCurrentUsername();
        Post post = new Post(title, anons, full_text, author);
        postRepozitori.save(post);
        return "redirect:/blog";
    }
    
    // Редактирование статьи
    @GetMapping("/blog/{id}/edit")
    public String blogEdit(@PathVariable(value = "id") long id, Model model) {
        Optional<Post> post = postRepozitori.findById(id);
        if (post.isEmpty()) {
            return "redirect:/blog";
        }
        
        // Проверка прав: только автор может редактировать
        String currentUser = getCurrentUsername();
        if (!post.get().getAuthor().equals(currentUser)) {
            return "redirect:/blog";
        }
        
        model.addAttribute("post", post.get());
        return "blogedit";
    }
    
    @PostMapping("/blog/{id}/edit")
    public String blogPostUpdate(@PathVariable(value = "id") long id,
                                 @RequestParam String title,
                                 @RequestParam String anons,
                                 @RequestParam String full_text) {
        Optional<Post> post = postRepozitori.findById(id);
        if (post.isPresent()) {
            Post article = post.get();
            
            // Проверка прав
            String currentUser = getCurrentUsername();
            if (article.getAuthor().equals(currentUser)) {
                article.setTitle(title);
                article.setAnons(anons);
                article.setText_full(full_text);
                postRepozitori.save(article);
            }
        }
        return "redirect:/blog";
    }
    
    // Удаление статьи
    @PostMapping("/blog/{id}/delete")
    public String blogDelete(@PathVariable(value = "id") long id) {
        Optional<Post> post = postRepozitori.findById(id);
        if (post.isPresent()) {
            String currentUser = getCurrentUsername();
            if (post.get().getAuthor().equals(currentUser)) {
                postRepozitori.deleteById(id);
            }
        }
        return "redirect:/blog";
    }
}