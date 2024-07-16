package com.omnizia.pubmedservice.controller;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.HcpDto;
import com.omnizia.pubmedservice.service.HcpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.omnizia.pubmedservice.constant.DbSelectorConstants.MCD;
import static com.omnizia.pubmedservice.constant.DbSelectorConstants.OLAM;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hcp")
public class HcpController {

  private final HcpService hcpService;

  @GetMapping
  public ResponseEntity<List<HcpDto>> getHcp() {
    try {
      DataSourceContextHolder.setDataSourceType(MCD);
      List<HcpDto> hcpData = hcpService.getHcp();
      log.info("Getting HCP data from {}", DataSourceContextHolder.getDataSourceType());
      return ResponseEntity.ok(hcpData);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  @GetMapping("/check-omnizia-id/{omniziaId}")
  public ResponseEntity<Boolean> checkOmniziaId(@PathVariable String omniziaId) {
    try {
      DataSourceContextHolder.setDataSourceType(OLAM);
      boolean isPresent = hcpService.checkOmniziaIdExists(omniziaId);
      return ResponseEntity.ok(isPresent);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }
}
