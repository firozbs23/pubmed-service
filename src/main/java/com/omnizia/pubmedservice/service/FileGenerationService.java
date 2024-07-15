package com.omnizia.pubmedservice.service;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.PubmedDataDto;
import com.omnizia.pubmedservice.util.DbSelectorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileGenerationService {

  private final JobDataService1 jobDataService;

  public byte[] getPubmedDataInCSV(UUID jobId) throws IOException {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorUtils.JOB_CONFIG);
      List<PubmedDataDto> pubmedData = jobDataService.getPubmedDataByJobId(jobId);

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
      DataSourceContextHolder.setDataSourceType(DbSelectorUtils.JOB_CONFIG);
      List<PubmedDataDto> pubmedData = jobDataService.getPubmedDataByJobId(jobId);

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
