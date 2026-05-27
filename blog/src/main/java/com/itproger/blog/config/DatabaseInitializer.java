package com.itproger.blog.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        // Создаём таблицу post если её нет
        String sql = """
            CREATE TABLE IF NOT EXISTS post (
                id BIGINT NOT NULL AUTO_INCREMENT,
                anons VARCHAR(1000),
                author VARCHAR(255),
                date DATETIME(6),
                image_name VARCHAR(255),
                text_full LONGTEXT,
                title VARCHAR(255),
                views INT NOT NULL,
                PRIMARY KEY (id)
            )
            """;
        
        try {
            jdbcTemplate.execute(sql);
            System.out.println("✅ Таблица post создана/проверена");
        } catch (Exception e) {
            System.out.println("❌ Ошибка при создании таблицы post: " + e.getMessage());
        }
    }
}
