package com.omnizia.pubmedservice.controller;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.service.HcpService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.omnizia.pubmedservice.constant.DbSelectorConstants.OLAM;

@Slf4j
@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hcp")
public class HcpController {

  private final HcpService hcpService;

  @GetMapping
  public ResponseEntity<?> getHcp(@RequestParam(value = "omnizia_id") Optional<String> omniziaId) {
    try {
      DataSourceContextHolder.setDataSourceType(OLAM);
      if (omniziaId.isPresent()) {
        var data = hcpService.getHcpByOmniziaId(omniziaId.get());
        return ResponseEntity.ok(data);
      }
      var data = hcpService.getAllHcp();
      return ResponseEntity.ok(data);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  @GetMapping("/{omnizia_id}/check")
  public ResponseEntity<Boolean> checkOmniziaId(@PathVariable("omnizia_id") String omniziaId) {
    try {
      DataSourceContextHolder.setDataSourceType(OLAM);
      boolean isPresent = hcpService.checkOmniziaIdExists(omniziaId);
      return ResponseEntity.ok(isPresent);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }
}
