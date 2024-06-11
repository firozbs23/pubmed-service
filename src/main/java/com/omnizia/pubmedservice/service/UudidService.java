package com.omnizia.pubmedservice.service;

import java.util.List;

import com.omnizia.pubmedservice.dto.UudidDto;
import com.omnizia.pubmedservice.entity.Uudid;
import com.omnizia.pubmedservice.mapper.UudidMapper;
import com.omnizia.pubmedservice.repository.UudidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UudidService {

  private final UudidRepository uudidRepository;

  public List<UudidDto> getUudids() {
    List<Uudid> uudids = uudidRepository.findAll();
    return UudidMapper.mapToUudidDto(uudids);
  }

  public List<UudidDto> getUudidsByOmniziaId(String omniziaId) {
    List<Uudid> uudids = uudidRepository.findAllByHcpId(omniziaId);
    return UudidMapper.mapToUudidDto(uudids);
  }
}
