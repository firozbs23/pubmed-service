package com.omnizia.pubmedservice.controller;

import com.omnizia.pubmedservice.constant.DbSelectorConstants;
import com.omnizia.pubmedservice.constant.DefaultConstants;
import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.ErrorDataDto;
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
  private final ErrorDataService errorDataService;

  @PostMapping(value = "/batch-job/start", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<JobStatusDto> getPubmedDataInBatchJob(
      @RequestParam(value = "file", required = false) MultipartFile file,
      @RequestParam("file_type") Optional<String> type,
      @RequestParam("id_column_name") Optional<String> idColumnName,
      @RequestParam("job_title") Optional<String> jobTitle,
      @RequestParam("omnizia_id") Optional<String> omniziaId) {

    String idColumn = idColumnName.orElse(DefaultConstants.OMNIZIA_ID);
    String fileType = type.orElse("csv");
    JobStatusDto data;

    if ((file == null || file.isEmpty()) && omniziaId.isPresent()) {
      data = pubmedService.processIdAndStartBatchJob(omniziaId.get(), jobTitle.orElse(UNKNOWN));
      log.info("Processing omniziaId : {} and start batch job", omniziaId.get());
    } else if (file != null && !file.isEmpty()) {
      List<String> omniziaIds = fileService.processFile(file, fileType, idColumn);
      data = pubmedService.startBatchJob(omniziaIds, jobTitle.orElse(file.getOriginalFilename()));
      log.info("Processing file {} and start batch job", file.getOriginalFilename());
    } else {
      throw new CustomException(
          "Invalid Request", "You need to provide a file or a valid omnizia id");
    }

    return ResponseEntity.ok(data);
  }

  @PostMapping(value = "/pubmed-job/start", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<JobStatusDto> getPubmedDataByPmid(
      @RequestParam(value = "file", required = false) MultipartFile file,
      @RequestParam("file_type") Optional<String> type,
      @RequestParam("id_column_name") Optional<String> idColumnName,
      @RequestParam("job_title") Optional<String> jobTitle,
      @RequestParam("pubmed_id") Optional<String> pubmedId) {

    String columnName = idColumnName.orElse(DefaultConstants.PUBMED_ID);
    String fileType = type.orElse("csv"); // File type csv is default
    JobStatusDto data = null;

    if ((file == null || file.isEmpty()) && pubmedId.isPresent()) {
      data = pubmedService.getPubmedDataByPmid(pubmedId.get(), jobTitle.orElse(UNKNOWN));
    } else if (file != null && !file.isEmpty()) {
      List<String> pubmedIds = fileService.processFile(file, fileType, columnName);
      data = pubmedService.getPubmedData(pubmedIds, jobTitle.orElse(file.getOriginalFilename()));
    }

    return ResponseEntity.ok(data);
  }

  @GetMapping("/job/data")
  public ResponseEntity<?> getPubmedDataInFileByJobId(
      @RequestParam("job_id") UUID jobId, @RequestParam("file_type") Optional<String> type)
      throws IOException {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);
      String fileType = type.orElse("json");

      byte[] fileInBytes;
      String fileFormat;
      HttpHeaders headers = new HttpHeaders();

      if (fileType.trim().equalsIgnoreCase("json")) {
        List<PubmedDataDto> pubmedData = pubmedDataService.getPubmedDataDto(jobId);
        return ResponseEntity.ok(pubmedData);
      } else if (fileType.trim().equalsIgnoreCase("csv")) {
        fileInBytes = fileService.getPubmedDataInCSV(jobId);
        fileFormat = ".csv";
        headers.setContentType(MediaType.parseMediaType("text/csv"));
      } else if (fileType.trim().equalsIgnoreCase("xlsx")) {
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

  @GetMapping("/job/status")
  public ResponseEntity<JobStatusDto> getPubmedJobStatus(@RequestParam("job_id") UUID jobId) {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);
      log.info("Api call to get job status : {}", jobId);
      JobStatusDto jobStatusDto = jobStatusService.getJobStatusDto(jobId);
      return ResponseEntity.ok(jobStatusDto);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  @GetMapping("/job/errors")
  public ResponseEntity<?> getWrongOmniziaIdList(
      @RequestParam("job_id") UUID jobId, @RequestParam("file_type") Optional<String> type) {
    String fileType = type.orElse("json");
    byte[] fileInBytes = new byte[0];
    String fileFormat = ".json";
    HttpHeaders headers = new HttpHeaders();

    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);
      List<ErrorDataDto> errors = errorDataService.getPubmedJobErrorList(jobId);

      if (fileType.equalsIgnoreCase("json")) {
        return ResponseEntity.ok().body(errors);
      } else if (fileType.equalsIgnoreCase("csv")) {
        fileInBytes = fileService.getErrorsInCSV(errors);
        fileFormat = ".csv";
        headers.setContentType(MediaType.parseMediaType("text/csv"));
      } else if (fileType.equalsIgnoreCase("xlsx")) {
        fileInBytes = fileService.getErrorsInXLSX(errors);
        fileFormat = ".xlsx";
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      } else {
        throw new CustomException(
            "Wrong File Type", "Please provide file_type. File type must be either csv or xlsx.");
      }
      String fileName;
      if (!errors.isEmpty() && StringUtils.isNotBlank(errors.getFirst().getJobTitle()))
        fileName = "Errors_" + errors.getFirst().getJobTitle().replace(" ", "-") + fileFormat;
      else fileName = "Errors_" + UNKNOWN + fileFormat;

      headers.setContentDispositionFormData("attachment", fileName);
      headers.setContentLength(fileInBytes.length);
      return ResponseEntity.ok().headers(headers).body(fileInBytes);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }
}
