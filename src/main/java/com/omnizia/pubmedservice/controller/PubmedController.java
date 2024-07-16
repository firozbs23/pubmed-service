package com.omnizia.pubmedservice.controller;

import com.omnizia.pubmedservice.constant.DbSelectorConstants;
import com.omnizia.pubmedservice.constant.DefaultConstants;
import com.omnizia.pubmedservice.constant.FileConstants;
import com.omnizia.pubmedservice.constant.HttpParamConstants;
import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.dto.PubmedDataDto;
import com.omnizia.pubmedservice.exception.CustomException;
import com.omnizia.pubmedservice.service.*;
import com.omnizia.pubmedservice.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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

  @PostMapping
  public ResponseEntity<JobStatusDto> getPubmedDataInBatchJob(
      @RequestParam(HttpParamConstants.PARAM_FILE) MultipartFile file,
      @RequestParam(HttpParamConstants.PARAM_FILE_TYPE) Optional<String> type,
      @RequestParam(HttpParamConstants.PARAM_ID_COLUMN_NAME) Optional<String> idColumnName,
      @RequestParam(HttpParamConstants.PARAM_JOB_TITLE) Optional<String> title) {

    String omniziaId = idColumnName.orElse(DefaultConstants.OMNIZIA_ID);
    String fileType = type.orElse(FileConstants.FILE_TYPE_CSV);
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
      @PathVariable String omniziaId,
      @RequestParam(HttpParamConstants.PARAM_JOB_TITLE) Optional<String> title) {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorConstants.OLAM);
      if (StringUtils.isNotBlank(omniziaId)) {
        throw new IllegalArgumentException();
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
      @RequestParam(HttpParamConstants.PARAM_FILE) MultipartFile file,
      @RequestParam(HttpParamConstants.PARAM_FILE_TYPE) Optional<String> type,
      @RequestParam(HttpParamConstants.PARAM_ID_COLUMN_NAME) Optional<String> idColumnName,
      @RequestParam(HttpParamConstants.PARAM_JOB_TITLE) Optional<String> title) {

    String pubmedId = idColumnName.orElse(DefaultConstants.PUBMED_ID);
    String fileType = type.orElse(FileConstants.FILE_TYPE_CSV); // File type csv is default
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
      @PathVariable String pubmedId,
      @RequestParam(HttpParamConstants.PARAM_JOB_TITLE) Optional<String> title) {
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
  public ResponseEntity<List<PubmedDataDto>> getPubmedDataByJobId(
      @RequestParam(HttpParamConstants.PARAM_JOB_ID) UUID jobId) {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);
      List<PubmedDataDto> pubmedData = pubmedDataService.getPubmedDataDto(jobId);
      return ResponseEntity.ok(pubmedData);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  @GetMapping("/file")
  public ResponseEntity<byte[]> getPubmedDataInFile(
      @RequestParam(HttpParamConstants.PARAM_JOB_ID) UUID jobId,
      @RequestParam(HttpParamConstants.PARAM_FILE_TYPE) Optional<String> type)
      throws IOException {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);
      String fileType = type.orElse(FileConstants.FILE_TYPE_CSV);

      byte[] fileInBytes;
      String fileFormat;
      HttpHeaders headers = new HttpHeaders();

      if (fileType.equalsIgnoreCase(FileConstants.FILE_TYPE_CSV)) {
        fileInBytes = fileService.getPubmedDataInCSV(jobId);
        fileFormat = FileConstants.FILE_FORMAT_CSV;
        headers.setContentType(MediaType.parseMediaType(FileConstants.MEDIA_TYPE_TEXT_CSV));
      } else if (fileType.equalsIgnoreCase(FileConstants.FILE_TYPE_XLSX)) {
        fileInBytes = fileService.getPubmedDataInXLSX(jobId);
        fileFormat = FileConstants.FILE_FORMAT_XLSX;
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      } else
        throw new CustomException(
            "Wrong File Type", "Please provide file_type. File type must be either csv or xlsx.");

      JobStatusDto jobStatusDto = jobStatusService.getJobStatusDto(jobId);
      String fileName = jobStatusDto.getJobTitle();

      if (StringUtils.isNotBlank(fileName)) fileName = fileName.replace(" ", "-") + fileFormat;
      else fileName = UNKNOWN + fileFormat;

      headers.setContentDispositionFormData(HttpParamConstants.PARAM_ATTACHMENT, fileName);
      headers.setContentLength(fileInBytes.length);
      return ResponseEntity.ok().headers(headers).body(fileInBytes);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  @GetMapping("/job-status")
  public ResponseEntity<JobStatusDto> getPubmedJobStatus(
      @RequestParam(HttpParamConstants.PARAM_JOB_ID) UUID jobId) {
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
