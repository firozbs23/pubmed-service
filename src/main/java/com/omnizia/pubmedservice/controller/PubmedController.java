package com.omnizia.pubmedservice.controller;

import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.service.FileProcessingService;
import com.omnizia.pubmedservice.service.PubmedService;
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

@Slf4j
@RestController
@RequestMapping("/api/v1/pubmed")
@RequiredArgsConstructor
public class PubmedController {

  private final FileProcessingService fileProcessingService;
  private final PubmedService pubmedService;

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
      List<String> omniziaIds = fileProcessingService.processFile(file, fileType, omniziaId);
      JobStatusDto jobStatus = pubmedService.startPubmedJob(omniziaIds, jobTitle);
      log.info("Current thread: {}", Thread.currentThread());
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
    List<String> omniziaIds = List.of(omniziaId);
    String jobTitle = title.orElse(StringUtils.EMPTY);
    JobStatusDto jobStatus = pubmedService.startPubmedJob(omniziaIds, jobTitle);
    return ResponseEntity.ok(jobStatus);
  }
}
