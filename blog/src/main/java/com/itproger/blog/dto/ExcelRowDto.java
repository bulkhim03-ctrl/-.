package com.itproger.blog.dto;

public class ExcelRowDto {
    private String title;
    private String anons;
    private String fullText;
    
    public ExcelRowDto() {}
    
    public ExcelRowDto(String title, String anons, String fullText) {
        this.title = title;
        this.anons = anons;
        this.fullText = fullText;
    }
    
    // Геттеры
    public String getTitle() { return title; }
    public String getAnons() { return anons; }
    public String getFullText() { return fullText; }
    
    // Сеттеры
    public void setTitle(String title) { this.title = title; }
    public void setAnons(String anons) { this.anons = anons; }
    public void setFullText(String fullText) { this.fullText = fullText; }
}