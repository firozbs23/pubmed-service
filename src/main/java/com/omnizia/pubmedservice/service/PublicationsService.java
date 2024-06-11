package com.omnizia.pubmedservice.service;

import java.util.List;

import com.omnizia.pubmedservice.dto.PublicationsDto;
import com.omnizia.pubmedservice.entity.Publications;
import com.omnizia.pubmedservice.mapper.PublicationsMapper;
import com.omnizia.pubmedservice.repository.PublicationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicationsService {

  private final PublicationsRepository publicationsRepository;

  public List<PublicationsDto> getPublications() {
    List<Publications> publications = publicationsRepository.findAll();
    return PublicationsMapper.mapToPublicationsDto(publications);
  }

  public List<PublicationsDto> getPublicationsByOmniziaId(String omniziaId) {
    List<Publications> publications = publicationsRepository.findAllByHcpViqId(omniziaId);
    return PublicationsMapper.mapToPublicationsDto(publications);
  }
}
