package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dto.HcpDto;
import com.omnizia.pubmedservice.entity.Hcp;
import com.omnizia.pubmedservice.mapper.HcpMapper;
import com.omnizia.pubmedservice.repository.HcpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HcpService {

  private final HcpRepository repository;

  public List<HcpDto> getHcp() {
    List<Hcp> hcpDataList = repository.findAll();
    return HcpMapper.mapToHcpDto(hcpDataList);
  }

  public boolean checkOmniziaIdExists(String omniziaId) {
    return repository.existsByViquiaId(omniziaId);
  }
}
