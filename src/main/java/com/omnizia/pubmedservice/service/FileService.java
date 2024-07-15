package com.omnizia.pubmedservice.service;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.PubmedDataDto;
import com.omnizia.pubmedservice.constant.DbSelectorConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.omnizia.pubmedservice.constant.FileConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

  private final PubmedDataService pubmedDataService;

  public List<String> processFile(MultipartFile file, String fileType, String columnName)
      throws IOException {
    fileType = fileType == null ? FILE_TYPE_CSV : fileType.trim().toLowerCase();
    columnName = columnName.trim();

    return switch (fileType) {
      case FILE_TYPE_CSV -> processCsvFile(file, columnName);
      case FILE_TYPE_XLSX -> processExcelFile(file, columnName);
      default -> throw new IllegalArgumentException("Unsupported file type: " + fileType);
    };
  }

  private List<String> processCsvFile(MultipartFile file, String columnName) throws IOException {
    List<String> idList = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      String headerLine = reader.readLine();
      if (headerLine == null) {
        throw new IOException("CSV file is empty");
      }

      // Find the index of the omnizia_id column
      String[] headers = headerLine.split(",");
      int idIndex = findColumnIndex(headers, columnName);

      // Process CSV content
      String line;
      while ((line = reader.readLine()) != null) {
        String[] values = line.split(",");
        if (values.length > idIndex) {
          // ids.add(values[idIndex]);
          idList.add(values[idIndex].replaceAll("^\"|\"$", "")); // Remove surrounding quotes if any
        }
      }
    }
    return idList;
  }

  private List<String> processExcelFile(MultipartFile file, String columnName) throws IOException {
    List<String> idList = new ArrayList<>();
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
            idList.add(getCellValueAsString(cell));
          }
        }
      }
    }
    return idList;
  }

  private int findColumnIndex(String[] headers, String columnName) {
    String columnNameWithQuotation = "\"" + columnName + "\"";
    for (int i = 0; i < headers.length; i++) {
      if (headers[i].trim().equals(columnName)
          || headers[i].trim().equals(columnNameWithQuotation)) {
        return i;
      }
    }
    throw new IllegalArgumentException("Column " + columnName + " not found");
  }

  private int findColumnIndex(Row headerRow, String columnName) {
    for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
      Cell cell = headerRow.getCell(i);
      if (cell.getStringCellValue().trim().equals(columnName)) {
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

  public byte[] getPubmedDataInCSV(UUID jobId) throws IOException {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);
      List<PubmedDataDto> pubmedData = pubmedDataService.getPubmedDataDto(jobId);

      CsvMapper csvMapper = new CsvMapper();
      /*CsvSchema schema = csvMapper.schemaFor(BioPythonDataDto.class).withHeader();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      csvMapper.writer(schema).writeValue(out, bioPythonDataDtos);
      return out.toByteArray();*/

      // Need to add column manually to keep column  arrangement fixed
      CsvSchema schema =
          CsvSchema.builder()
              .addColumn("transaction_viq_id")
              .addColumn("hcp_viq_id")
              .addColumn("country_iso2")
              .addColumn("specialty_code")
              .addColumn("publication_id")
              .addColumn("title")
              .addColumn("journal")
              .addColumn("publication_date")
              .addColumn("abstract")
              .addColumn("hcp_role")
              .addColumn("publication_type")
              .addColumn("issn")
              .addColumn("url")
              .addColumn("gds_tag_viq_id")
              .addColumn("hcp_role_viq_id")
              .addColumn("key")
              .addColumn("created_by_job")
              .addColumn("updated_by_job")
              .addColumn("created_at")
              .addColumn("updated_at")
              .addColumn("first_name")
              .addColumn("last_name")
              .addColumn("initials")
              .addColumn("full_name")
              .addColumn("affiliations")
              .addColumn("pmcid")
              .addColumn("doi")
              .addColumn("mesh_terms")
              .addColumn("timestamp")
              .addColumn("job_id")
              .addColumn("job_title")
              .addColumn("publication_platform")
              .addColumn("search_name")
              .build()
              .withHeader();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ObjectWriter writer = csvMapper.writer(schema);
      writer.writeValue(out, pubmedData);
      return out.toByteArray();
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  public byte[] getPubmedDataInXLSX(UUID jobId) throws IOException {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);
      List<PubmedDataDto> pubmedData = pubmedDataService.getPubmedDataDto(jobId);

      // Create a new workbook and sheet
      Workbook workbook = new XSSFWorkbook();
      Sheet sheet = workbook.createSheet("Pubmed Data");

      // Create header row
      Row headerRow = sheet.createRow(0);
      String[] headers = {
        "transaction_viq_id",
        "hcp_viq_id",
        "country_iso2",
        "specialty_code",
        "publication_id",
        "title",
        "journal",
        "publication_date",
        "abstract",
        "hcp_role",
        "publication_type",
        "issn",
        "url",
        "gds_tag_viq_id",
        "hcp_role_viq_id",
        "key",
        "created_by_job",
        "updated_by_job",
        "created_at",
        "updated_at",
        "first_name",
        "last_name",
        "initials",
        "full_name",
        "affiliations",
        "pmcid",
        "doi",
        "mesh_terms",
        "timestamp",
        "job_id",
        "job_title",
        "publication_platform",
        "search_name"
      };

      for (int i = 0; i < headers.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(headers[i]);
      }

      // Populate rows with data
      int rowIdx = 1;
      for (PubmedDataDto data : pubmedData) {
        Row row = sheet.createRow(rowIdx++);
        row.createCell(0).setCellValue(data.getTransactionViqId());
        row.createCell(1).setCellValue(data.getHcpViqId());
        row.createCell(2).setCellValue(data.getCountryIso2());
        row.createCell(3).setCellValue(data.getSpecialtyCode());
        row.createCell(4).setCellValue(data.getPublicationId());
        row.createCell(5).setCellValue(data.getTitle());
        row.createCell(6).setCellValue(data.getJournal());
        row.createCell(7).setCellValue(data.getPublicationDate());
        row.createCell(8).setCellValue(data.getAbstractValue());
        row.createCell(9).setCellValue(data.getHcpRole());
        row.createCell(10).setCellValue(data.getPublicationType());
        row.createCell(11).setCellValue(data.getIssn());
        row.createCell(12).setCellValue(data.getUrl());
        row.createCell(13).setCellValue(data.getGdsTagViqId());
        row.createCell(14).setCellValue(data.getHcpRoleViqId());
        row.createCell(15).setCellValue(data.getKey());
        row.createCell(16).setCellValue(data.getCreatedByJob());
        row.createCell(17).setCellValue(data.getUpdatedByJob());
        row.createCell(18).setCellValue(data.getCreatedAt());
        row.createCell(19).setCellValue(data.getUpdatedAt());
        row.createCell(20).setCellValue(data.getFirstName());
        row.createCell(21).setCellValue(data.getLastName());
        row.createCell(22).setCellValue(data.getInitials());
        row.createCell(23).setCellValue(data.getFullName());
        row.createCell(24).setCellValue(data.getAffiliations());
        row.createCell(25).setCellValue(data.getPmcid());
        row.createCell(26).setCellValue(data.getDoi());
        row.createCell(27).setCellValue(data.getMeshTerms());
        row.createCell(28).setCellValue(data.getTimestamp());
        row.createCell(29).setCellValue(data.getJobId());
        row.createCell(30).setCellValue(data.getJobTitle());
        row.createCell(31).setCellValue(data.getPublicationPlatform());
        row.createCell(32).setCellValue(data.getSearchName());
      }

      // Write the workbook to a byte array output stream
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      workbook.write(out);
      workbook.close();
      return out.toByteArray();
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }
}
