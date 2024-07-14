package com.omnizia.pubmedservice.controller;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.dto.PubmedDataDto;
import com.omnizia.pubmedservice.exception.CustomException;
import com.omnizia.pubmedservice.service.*;
import com.omnizia.pubmedservice.util.DbSelectorUtils;
import com.omnizia.pubmedservice.util.FileTypeUtils;
import com.omnizia.pubmedservice.util.StringUtils;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/pubmed")
@RequiredArgsConstructor
public class PubmedController {

  private final FileProcessingService fileProcessingService;
  private final FileGenerationService fileGenerationService;
  private final PubmedService pubmedService;
  private final JobDataService jobDataService;

  @PostMapping
  public ResponseEntity<JobStatusDto> getPubmedDataInBatchJob(
      @RequestParam("file") MultipartFile file,
      @RequestParam("file_type") Optional<String> type,
      @RequestParam("id_column_name") Optional<String> idColumnName,
      @RequestParam("job_title") Optional<String> title) {

    String omniziaId = idColumnName.orElse("omnizia_id");
    String fileType = type.orElse(FileTypeUtils.CSV);
    String jobTitle = title.orElse(StringUtils.EMPTY);

    try {
      List<String> omniziaIds =
          fileProcessingService.processFile(file, fileType.trim(), omniziaId.trim());
      log.info("Total omnizia id found : {}", omniziaIds.size());
      JobStatusDto jobStatus = pubmedService.startPubmedBatchJob(omniziaIds, jobTitle.trim());
      return ResponseEntity.ok(jobStatus);
    } catch (IOException e) {
      throw new RuntimeException("Error while processing file", e);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid file type", e);
    }
  }

  @PostMapping("/{omnizia_id}")
  public ResponseEntity<JobStatusDto> processFile(
      @PathVariable("omnizia_id") String omniziaId,
      @RequestParam("job_title") Optional<String> title) {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorUtils.OLAM);
      List<String> omniziaIds = List.of(omniziaId);
      String jobTitle = title.orElse(StringUtils.EMPTY);
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

    String omniziaId = idColumnName.orElse("publication_id");
    String fileType = type.orElse(FileTypeUtils.CSV);
    String jobTitle = title.orElse(StringUtils.EMPTY);

    try {
      List<String> publicationIds =
          fileProcessingService.processFile(file, fileType.trim(), omniziaId.trim());
      log.info("Total publication_id found : {}", publicationIds.size());
      JobStatusDto jobStatus = pubmedService.findPubmedDataByPmid(publicationIds, jobTitle.trim());
      return ResponseEntity.ok(jobStatus);
    } catch (IOException e) {
      throw new RuntimeException("Error while processing file", e);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid file type", e);
    }
  }

  @PostMapping("/pmid/{pmid}")
  public ResponseEntity<JobStatusDto> getPubmedDataByPmid(
      @PathVariable("pmid") String pmid, @RequestParam("job_title") Optional<String> title) {
    try {
      List<String> pubmedIds = List.of(pmid);
      String jobTitle = title.orElse(StringUtils.EMPTY);
      JobStatusDto jobStatus = pubmedService.findPubmedDataByPmid(pubmedIds, jobTitle.trim());
      return ResponseEntity.ok(jobStatus);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid file type", e);
    }
  }

  @GetMapping
  public ResponseEntity<List<PubmedDataDto>> getPubmedDataByJobId(
      @RequestParam("job_id") UUID jobId) {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorUtils.JOB_CONFIG);
      List<PubmedDataDto> pubmedData = jobDataService.getPubmedDataByJobId(jobId);
      return ResponseEntity.ok(pubmedData);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  @GetMapping("/file")
  public ResponseEntity<byte[]> getPubmedDataInFile(
      @RequestParam("job_id") UUID jobId, @RequestParam("file_type") Optional<String> type)
      throws IOException {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorUtils.JOB_CONFIG);
      String fileType = type.orElse(FileTypeUtils.CSV);

      byte[] fileInBytes;
      String fileFormat;
      HttpHeaders headers = new HttpHeaders();

      if (fileType.equalsIgnoreCase("csv")) {
        fileInBytes = fileGenerationService.getPubmedDataInCSV(jobId);
        fileFormat = ".csv";
        headers.setContentType(MediaType.parseMediaType("text/csv"));
      } else if (fileType.equalsIgnoreCase("xlsx")) {
        fileInBytes = fileGenerationService.getPubmedDataInXLSX(jobId);
        fileFormat = ".xlsx";
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
      } else
        throw new CustomException(
            "Wrong File Type", "Please provide file_type. File type must be either csv or xlsx.");

      JobStatusDto jobStatusDto = jobDataService.getPubmedJobStatus(jobId);
      String fileName = jobStatusDto.getJobTitle();

      if (fileName != null && !fileName.trim().isEmpty())
        fileName = fileName.replace(" ", "-") + fileFormat;
      else fileName = "data" + fileFormat;

      headers.setContentDispositionFormData("attachment", fileName);
      headers.setContentLength(fileInBytes.length);
      return ResponseEntity.ok().headers(headers).body(fileInBytes);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  @GetMapping("/job-status")
  public ResponseEntity<JobStatusDto> getPubmedJobStatus(@RequestParam("job_id") UUID jobId) {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorUtils.JOB_CONFIG);
      log.info("Api call to get job status : {}", jobId);
      JobStatusDto jobStatusDto = jobDataService.getPubmedJobStatus(jobId);
      return ResponseEntity.ok(jobStatusDto);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }
}
