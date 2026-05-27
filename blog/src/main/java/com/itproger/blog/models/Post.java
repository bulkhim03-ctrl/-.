package com.itproger.blog.models;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 500)
    private String title;
    
    @Column(length = 1000)
    private String anons;
    
    @Column(columnDefinition = "LONGTEXT")
    private String text_full;
    
    private int views;
    private LocalDateTime date;
    private String author;
    private String imageName;
    
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAnons() { return anons; }
    public void setAnons(String anons) { this.anons = anons; }
    
    public String getText_full() { return text_full; }
    public void setText_full(String text_full) { this.text_full = text_full; }
    
    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }
    
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    
    // Конструкторы
    public Post() {
        this.date = LocalDateTime.now();
        this.views = 0;
    }
    
    public Post(String title, String anons, String full_text, String author) {
        this.title = title;
        this.anons = anons;
        this.text_full = full_text;
        this.author = author;
        this.date = LocalDateTime.now();
        this.views = 0;
        this.imageName = null;
    }
    
    public Post(String title, String anons, String full_text, String author, String imageName) {
        this.title = title;
        this.anons = anons;
        this.text_full = full_text;
        this.author = author;
        this.date = LocalDateTime.now();
        this.views = 0;
        this.imageName = imageName;
    }
}