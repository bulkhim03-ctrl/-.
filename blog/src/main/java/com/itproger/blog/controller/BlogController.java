package com.itproger.blog.controller;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.itproger.blog.models.Post;
import com.itproger.blog.repo.PostRepozitori;
import com.itproger.blog.services.AuthService;
import com.itproger.blog.services.ExcelReportService;

@Controller
public class BlogController {

    @Autowired
    private PostRepozitori postRepozitori;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private ExcelReportService excelReportService;
    
    private String getCurrentUsername() {
        try {
            return authService.getUserLoggedInfo().username();
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/blog")
    public String blogMain(Model model) {
        Iterable<Post> posts = postRepozitori.findAll();
        model.addAttribute("posts", posts);
        model.addAttribute("currentUser", getCurrentUsername());
        return "blogmain";
    }
    
    @GetMapping("/blog/{id}")
    public String blogDetail(@PathVariable(value = "id") long id, Model model) {
        Optional<Post> post = postRepozitori.findById(id);
        if (post.isEmpty()) {
            return "redirect:/blog";
        }
        
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
    
    @GetMapping("/blog/{id}/edit")
    public String blogEdit(@PathVariable(value = "id") long id, Model model) {
        Optional<Post> post = postRepozitori.findById(id);
        if (post.isEmpty()) {
            return "redirect:/blog";
        }
        
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
    
    // НОВЫЙ МЕТОД: Экспорт в Excel
    @GetMapping("/blog/export-excel")
    public ResponseEntity<byte[]> exportToExcel() {
        try {
            String currentUser = getCurrentUsername();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            ByteArrayInputStream excelStream = excelReportService.exportUserPostsToExcel(currentUser);
            
            String filename = "my_posts_" + currentUser + "_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelStream.readAllBytes());
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}