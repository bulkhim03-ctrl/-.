package com.itproger.blog.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title, anons, text_full;
    private int views;

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id=id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public String getAnons(){
        return anons;
    }

    public void setAnons(String anons){
        this.anons=anons;
    }

    public String getText_full(){
        return text_full;
    }

    public void setText_full(String text_full){
        this.text_full=text_full;
    }

    public int getViews(){
        return views;
    }

    public void setViews(int views){
        this.views=views;
    }
}
