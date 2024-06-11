package com.omnizia.pubmedservice.controller;

import com.omnizia.pubmedservice.service.FileProcessingService;
import com.omnizia.pubmedservice.util.FileType;
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

  @PostMapping("/upload")
  public ResponseEntity<?> uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("file_type") Optional<String> type,
      @RequestParam("id_column") Optional<String> idColumn,
      @RequestParam("job_title") Optional<String> title) {

    String omniziaId = idColumn.orElse("omnizia_id");
    String fileType = type.orElse(FileType.CSV);
    String jobTitle = title.orElse(StringUtils.EMPTY);

    try {
      List<String> omniziaIds = fileProcessingService.processFile(file, fileType, omniziaId);
      return ResponseEntity.ok(omniziaIds);
    } catch (IOException e) {
      return ResponseEntity.status(500).body("Failed to process file: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @PostMapping("/{omnizia_id}")
  public ResponseEntity<?> processFile(@PathVariable("omnizia_id") String omniziaId) {
    List<String> omniziaIds = List.of(omniziaId);
    return ResponseEntity.ok(omniziaIds);
  }
}
