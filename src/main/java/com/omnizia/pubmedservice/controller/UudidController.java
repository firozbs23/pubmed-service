package com.omnizia.pubmedservice.controller;

import java.util.List;
import java.util.Optional;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.UudidDto;
import com.omnizia.pubmedservice.service.UudidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.omnizia.pubmedservice.constant.DbSelectorConstants.MCD;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/uudid")
public class UudidController {

  private final UudidService uudidService;

  @GetMapping
  ResponseEntity<List<UudidDto>> getUudids(@RequestParam("omnizia_id") Optional<String> omniziaId) {
    try {
      DataSourceContextHolder.setDataSourceType(MCD);
      List<UudidDto> data;
      if (omniziaId.isPresent()) {
        data = uudidService.getUudidsByOmniziaId(omniziaId.get());
      } else {
        data = uudidService.getAllUudid();
      }
      return ResponseEntity.ok(data);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }
}
