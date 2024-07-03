package com.omnizia.pubmedservice.controller;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.dto.PubmedDto;
import com.omnizia.pubmedservice.service.FileProcessingService;
import com.omnizia.pubmedservice.service.JobDataService;
import com.omnizia.pubmedservice.service.PubmedService;
import com.omnizia.pubmedservice.util.DbSelectorUtils;
import com.omnizia.pubmedservice.util.FileTypeUtils;
import com.omnizia.pubmedservice.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private final PubmedService pubmedService;
  private final JobDataService jobDataService;

  @PostMapping
  public ResponseEntity<JobStatusDto> uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("file_type") Optional<String> type,
      @RequestParam("id_column_name") Optional<String> idColumnName,
      @RequestParam("job_title") Optional<String> title) {

    String omniziaId = idColumnName.orElse("omnizia_id");
    String fileType = type.orElse(FileTypeUtils.CSV);
    String jobTitle = title.orElse(StringUtils.EMPTY);

    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorUtils.OLAM);
      List<String> omniziaIds = fileProcessingService.processFile(file, fileType, omniziaId);
      log.info("Total omnizia id found : {}", omniziaIds.size());
      JobStatusDto jobStatus = pubmedService.startPubmedJob(omniziaIds, jobTitle);
      return ResponseEntity.ok(jobStatus);
    } catch (IOException e) {
      throw new RuntimeException("Error while processing file", e);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid file type", e);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
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
      JobStatusDto jobStatus = pubmedService.startPubmedJob(omniziaIds, jobTitle);
      return ResponseEntity.ok(jobStatus);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  @GetMapping
  public ResponseEntity<List<PubmedDto>> getPubmedByJobId(
      @RequestParam("job_id") UUID jobId,
      @RequestParam("file_type") Optional<String> type,
      @RequestParam("job_title") Optional<String> title) {

    String fileType = type.orElse(FileTypeUtils.CSV);
    String jobTitle = title.orElse(StringUtils.EMPTY);

    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorUtils.JOB_CONFIG);
      List<PubmedDto> pubmedData = jobDataService.getPubmedDataByJobId(jobId);
      log.info("Current thread: {}", Thread.currentThread());
      return ResponseEntity.ok(pubmedData);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }
}
