package com.omnizia.pubmedservice.controller;

import com.omnizia.pubmedservice.constant.DbSelectorConstants;
import com.omnizia.pubmedservice.constant.DefaultConstants;
import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.dto.PubmedDataDto;
import com.omnizia.pubmedservice.exception.CustomException;
import com.omnizia.pubmedservice.service.*;
import com.omnizia.pubmedservice.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.omnizia.pubmedservice.constant.DefaultConstants.UNKNOWN;

@Slf4j
@RestController
@RequestMapping("/api/v1/pubmed")
@RequiredArgsConstructor
public class PubmedController {

  private final FileService fileService;
  private final PubmedService pubmedService;
  private final JobStatusService jobStatusService;
  private final PubmedDataService pubmedDataService;
  private final HcpService hcpService;

  @PostMapping
  public ResponseEntity<JobStatusDto> getPubmedDataInBatchJob(
      @RequestParam("file") MultipartFile file,
      @RequestParam("file_type") Optional<String> type,
      @RequestParam("id_column_name") Optional<String> idColumnName,
      @RequestParam("job_title") Optional<String> title) {

    String omniziaId = idColumnName.orElse(DefaultConstants.OMNIZIA_ID);
    String fileType = type.orElse("csv");
    String jobTitle = title.orElse(file.getOriginalFilename());

    try {
      List<String> omniziaIds = fileService.processFile(file, fileType, omniziaId);
      log.info("Total omnizia id found : {}", omniziaIds.size());
      JobStatusDto jobStatus = pubmedService.startPubmedBatchJob(omniziaIds, jobTitle);
      return ResponseEntity.ok(jobStatus);
    } catch (IOException e) {
      throw new RuntimeException("Error while processing file", e);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid file type", e);
    }
  }

  @PostMapping("/{omniziaId}")
  public ResponseEntity<JobStatusDto> processFile(
      @PathVariable String omniziaId, @RequestParam("job_title") Optional<String> title) {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorConstants.OLAM);
      if (StringUtils.isBlank(omniziaId) || !hcpService.checkOmniziaIdExists(omniziaId)) {
        throw new CustomException(
            HttpStatus.BAD_REQUEST.name(), "Your provided omnizia_id is wrong or does not exist");
      }

      List<String> omniziaIds = List.of(omniziaId.trim());
      String jobTitle = StringUtils.getStringOrDefault(title.orElse(null), UNKNOWN);
      JobStatusDto jobStatus = pubmedService.startPubmedBatchJob(omniziaIds, jobTitle);
      return ResponseEntity.ok(jobStatus);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  @PostMapping("/pmid")
  public ResponseEntity<JobStatusDto> getPubmedDataByPmid(
      @RequestParam("file") MultipartFile file,
      @RequestParam("file_type") Optional<String> type,
      @RequestParam("id_column_name") Optional<String> idColumnName,
      @RequestParam("job_title") Optional<String> title) {

    String pubmedId = idColumnName.orElse(DefaultConstants.PUBMED_ID);
    String fileType = type.orElse("csv"); // File type csv is default
    String jobTitle = title.orElse(file.getOriginalFilename());

    try {
      List<String> pubmedIds = fileService.processFile(file, fileType, pubmedId);
      log.info("Total publication_id found : {}", pubmedIds.size());
      JobStatusDto jobStatus = pubmedService.findPubmedDataInBackground(pubmedIds, jobTitle);
      return ResponseEntity.ok(jobStatus);
    } catch (IOException e) {
      throw new RuntimeException("Error while processing file", e);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid file type", e);
    }
  }

  @PostMapping("/pmid/{pubmedId}")
  public ResponseEntity<JobStatusDto> getPubmedDataByPmid(
      @PathVariable String pubmedId, @RequestParam("job_title") Optional<String> title) {
    try {
      List<String> pubmedIds = List.of(pubmedId);
      String jobTitle = title.orElse(UNKNOWN);
      JobStatusDto jobStatus = pubmedService.findPubmedDataInBackground(pubmedIds, jobTitle.trim());
      return ResponseEntity.ok(jobStatus);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid file type", e);
    }
  }

  @GetMapping
  public ResponseEntity<List<PubmedDataDto>> getPubmedDataInJsonByJobId(
      @RequestParam("job_id") UUID jobId) {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);
      List<PubmedDataDto> pubmedData = pubmedDataService.getPubmedDataDto(jobId);
      return ResponseEntity.ok(pubmedData);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  @GetMapping("/file")
  public ResponseEntity<byte[]> getPubmedDataInFileByJobId(
      @RequestParam("job_id") UUID jobId, @RequestParam("file_type") Optional<String> type)
      throws IOException {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);
      String fileType = type.orElse("csv");

      byte[] fileInBytes;
      String fileFormat;
      HttpHeaders headers = new HttpHeaders();

      if (fileType.equalsIgnoreCase("csv")) {
        fileInBytes = fileService.getPubmedDataInCSV(jobId);
        fileFormat = ".csv";
        headers.setContentType(MediaType.parseMediaType("text/csv"));
      } else if (fileType.equalsIgnoreCase("xlsx")) {
        fileInBytes = fileService.getPubmedDataInXLSX(jobId);
        fileFormat = ".xlsx";
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      } else
        throw new CustomException(
            "Wrong File Type", "Please provide file_type. File type must be either csv or xlsx.");

      JobStatusDto jobStatusDto = jobStatusService.getJobStatusDto(jobId);
      String fileName = jobStatusDto.getJobTitle();

      if (StringUtils.isNotBlank(fileName)) fileName = fileName.replace(" ", "-") + fileFormat;
      else fileName = UNKNOWN + fileFormat;

      headers.setContentDispositionFormData("attachment", fileName);
      headers.setContentLength(fileInBytes.length);
      return ResponseEntity.ok().headers(headers).body(fileInBytes);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  @GetMapping("/job-status/{jobId}")
  public ResponseEntity<JobStatusDto> getPubmedJobStatus(@PathVariable UUID jobId) {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);
      log.info("Api call to get job status : {}", jobId);
      JobStatusDto jobStatusDto = jobStatusService.getJobStatusDto(jobId);
      return ResponseEntity.ok(jobStatusDto);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }
}
