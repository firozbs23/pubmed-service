package com.omnizia.pubmedservice.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import static com.omnizia.pubmedservice.util.FileType.*;

@Service
public class FileProcessingService {

  public List<String> processFile(MultipartFile file, String fileType, String columnName)
      throws IOException {
    return switch (fileType.toLowerCase()) {
      case CSV -> processCsvFile(file, columnName);
      case XLSX -> processExcelFile(file, columnName);
      default -> throw new IllegalArgumentException("Unsupported file type: " + fileType);
    };
  }

  private List<String> processCsvFile(MultipartFile file, String columnName) throws IOException {
    List<String> omniziaIds = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      String headerLine = reader.readLine();
      if (headerLine == null) {
        throw new IOException("CSV file is empty");
      }

      // Find the index of the omnizia_id column
      String[] headers = headerLine.split(",");
      int omniziaIdIndex = findColumnIndex(headers, columnName);

      // Process CSV content
      String line;
      while ((line = reader.readLine()) != null) {
        String[] values = line.split(",");
        if (values.length > omniziaIdIndex) {
          omniziaIds.add(values[omniziaIdIndex]);
        }
      }
    }
    return omniziaIds;
  }

  private List<String> processExcelFile(MultipartFile file, String columnName) throws IOException {
    List<String> omniziaIds = new ArrayList<>();
    try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
      Sheet sheet = workbook.getSheetAt(0);
      if (sheet.getPhysicalNumberOfRows() > 0) {
        Row headerRow = sheet.getRow(0);
        int omniziaIdIndex = findColumnIndex(headerRow, columnName);

        // Process Excel content
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
          Row row = sheet.getRow(i);
          Cell cell = row.getCell(omniziaIdIndex);
          if (cell != null) {
            omniziaIds.add(getCellValueAsString(cell));
          }
        }
      }
    }
    return omniziaIds;
  }

  private int findColumnIndex(String[] headers, String columnName) {
    for (int i = 0; i < headers.length; i++) {
      if (headers[i].trim().equalsIgnoreCase(columnName)) {
        return i;
      }
    }
    throw new IllegalArgumentException("Column " + columnName + " not found");
  }

  private int findColumnIndex(Row headerRow, String columnName) {
    for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
      Cell cell = headerRow.getCell(i);
      if (cell.getStringCellValue().trim().equalsIgnoreCase(columnName)) {
        return i;
      }
    }
    throw new IllegalArgumentException("Column " + columnName + " not found");
  }

  private String getCellValueAsString(Cell cell) {
    return switch (cell.getCellType()) {
      case STRING -> cell.getStringCellValue();
      case NUMERIC -> String.valueOf(cell.getNumericCellValue());
      default -> "";
    };
  }
}
