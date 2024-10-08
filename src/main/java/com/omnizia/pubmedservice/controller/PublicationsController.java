package com.omnizia.pubmedservice.controller;

import java.util.List;
import java.util.Optional;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.PublicationsDto;
import com.omnizia.pubmedservice.service.PublicationsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.omnizia.pubmedservice.constant.DbSelectorConstants.OLAM;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/publications")
public class PublicationsController {

  private final PublicationsService pubService;

  @GetMapping
  ResponseEntity<List<PublicationsDto>> getPublications(
      @RequestParam("omnizia_id") Optional<String> omniziaId) {
    try {
      DataSourceContextHolder.setDataSourceType(OLAM);
      List<PublicationsDto> publications;

      if (omniziaId.isPresent()) {
        publications = pubService.getPublicationsByOmniziaId(omniziaId.get());
      } else {
        publications = pubService.getPublications();
      }
      log.info("Getting publications from {}", DataSourceContextHolder.getDataSourceType());
      return ResponseEntity.ok(publications);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }
}
