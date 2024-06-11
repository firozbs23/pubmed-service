package com.omnizia.pubmedservice.controller;

import java.util.List;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.UudidDto;
import com.omnizia.pubmedservice.service.UudidService;
import com.omnizia.pubmedservice.util.DbSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/uudid")
public class UudidController {

  private final UudidService uudidService;

  @GetMapping
  ResponseEntity<List<UudidDto>> getUudids() {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelector.MCD);
      List<UudidDto> publications = uudidService.getUudids();
      log.info("Getting uudids from {}", DataSourceContextHolder.getDataSourceType());
      return ResponseEntity.ok(publications);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  @GetMapping("/{omnizia_id}")
  public ResponseEntity<List<UudidDto>> getUudidByOmniziaId(
      @PathVariable("omnizia_id") String omniziaId) {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelector.MCD);
      List<UudidDto> publications = uudidService.getUudidsByOmniziaId(omniziaId);
      log.info("Getting uudids by omnizia_id from {}", DataSourceContextHolder.getDataSourceType());
      return ResponseEntity.ok(publications);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }
}
