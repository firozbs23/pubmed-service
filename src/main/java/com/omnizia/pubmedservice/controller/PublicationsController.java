package com.omnizia.pubmedservice.controller;

import java.util.List;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.PublicationsDto;
import com.omnizia.pubmedservice.service.PublicationsService;
import com.omnizia.pubmedservice.util.DbSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/publications")
public class PublicationsController {

  private final PublicationsService pubService;

  @GetMapping
  ResponseEntity<List<PublicationsDto>> getPublications() {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelector.MCD);
      List<PublicationsDto> publications = pubService.getPublications();
      log.info("Getting publications from {}", DataSourceContextHolder.getDataSourceType());
      return ResponseEntity.ok(publications);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  @GetMapping("/{omnizia_id}")
  public ResponseEntity<List<PublicationsDto>> getPublicationsByOmniziaId(
      @PathVariable("omnizia_id") String omniziaId) {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelector.MCD);
      List<PublicationsDto> publications = pubService.getPublicationsByOmniziaId(omniziaId);

      String selectedDb = DataSourceContextHolder.getDataSourceType();
      log.info("Getting publications by omnizia_id from {}", selectedDb);

      return ResponseEntity.ok(publications);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }
}
