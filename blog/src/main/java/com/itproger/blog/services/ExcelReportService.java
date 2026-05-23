package com.itproger.blog.services;  // ВАЖНО: services (с S)

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itproger.blog.models.Post;
import com.itproger.blog.repo.PostRepozitori;

@Service
public class ExcelReportService {
    
    @Autowired
    private PostRepozitori postRepozitori;
    
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    
    public ByteArrayInputStream exportUserPostsToExcel(String username) {
        List<Post> userPosts = postRepozitori.findByAuthor(username);
        
        try (Workbook workbook = new XSSFWorkbook(); 
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Мои статьи");
            
            // Стиль заголовков
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Заголовки
            Row headerRow = sheet.createRow(0);
            String[] columns = {"№", "Название статьи", "Полный текст", "Просмотры", "Дата создания"};
            
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }
            
            // Данные
            int rowNum = 1;
            for (Post post : userPosts) {
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(rowNum);
                row.createCell(1).setCellValue(post.getTitle());
                row.createCell(2).setCellValue(post.getText_full());
                row.createCell(3).setCellValue(post.getViews());
                row.createCell(4).setCellValue(post.getDate() != null ? 
                    post.getDate().format(DATE_FORMATTER) : "");
                rowNum++;
            }
            
            // Автоширина
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
            
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании Excel: " + e.getMessage());
        }
    }
}