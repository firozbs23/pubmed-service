package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dto.PubmedDataDto;
import com.omnizia.pubmedservice.entity.PubmedData;
import com.omnizia.pubmedservice.mapper.PubmedDataMapper;
import com.omnizia.pubmedservice.repository.PubmedDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PubmedDataService {

  private final PubmedDataRepository repository;

  public PubmedDataDto getPubmedData(UUID uuid) {
    PubmedData pubmedData = repository.findById(uuid).orElse(null);
    if (pubmedData == null) return null;
    return PubmedDataMapper.mapToPubmedDataDto(pubmedData);
  }

  public PubmedDataDto savePubmedData(PubmedDataDto pubmedData) {
    PubmedData newPubmedData = PubmedDataMapper.mapToPubmedData(pubmedData);
    PubmedData savedPubmed = repository.save(newPubmedData);
    return PubmedDataMapper.mapToPubmedDataDto(savedPubmed);
  }
}
