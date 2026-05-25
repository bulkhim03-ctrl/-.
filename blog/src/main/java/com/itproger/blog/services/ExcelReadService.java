package com.itproger.blog.services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itproger.blog.dto.ExcelRowDto;

@Service
public class ExcelReadService {
    
    public List<ExcelRowDto> readExcelFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        
        List<ExcelRowDto> dataList = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook;
            
            if (extension.equals("xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else if (extension.equals("xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                throw new IllegalArgumentException("Неподдерживаемый формат файла");
            }
            
            Sheet sheet = workbook.getSheetAt(0);
            boolean isFirstRow = true;
            
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }
                
                try {
                    String title = getCellValue(row.getCell(0));
                    String anons = getCellValue(row.getCell(1));
                    String fullText = getCellValue(row.getCell(2));
                    
                    if (title != null && !title.trim().isEmpty()) {
                        dataList.add(new ExcelRowDto(title, anons, fullText));
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка при чтении строки: " + e.getMessage());
                }
            }
            
            workbook.close();
        }
        
        return dataList;
    }
    
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}