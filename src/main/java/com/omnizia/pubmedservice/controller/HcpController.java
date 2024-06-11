package com.omnizia.pubmedservice.controller;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.HcpDto;
import com.omnizia.pubmedservice.service.HcpService;
import com.omnizia.pubmedservice.util.DbSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hcp")
public class HcpController {

  private final HcpService hcpService;

  @GetMapping
  public ResponseEntity<List<HcpDto>> getHcp() {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelector.MCD);
      List<HcpDto> hcpData = hcpService.getHcp();
      log.info("Getting HCP data from {}", DataSourceContextHolder.getDataSourceType());
      return ResponseEntity.ok(hcpData);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }
}
