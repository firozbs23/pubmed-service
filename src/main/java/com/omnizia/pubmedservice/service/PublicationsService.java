package com.omnizia.pubmedservice.service;

import java.util.List;

import com.omnizia.pubmedservice.dto.PublicationsDto;
import com.omnizia.pubmedservice.entity.Publications;
import com.omnizia.pubmedservice.exception.CustomException;
import com.omnizia.pubmedservice.mapper.PublicationsMapper;
import com.omnizia.pubmedservice.repository.PublicationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicationsService {

  private final PublicationsRepository repository;
  private final HcpService hcpService;

  public List<PublicationsDto> getPublications() {
    List<Publications> publications = repository.findAll();
    return PublicationsMapper.mapToPublicationsDto(publications);
  }

  public List<PublicationsDto> getPublicationsByOmniziaId(String omniziaId) {
    if (!hcpService.checkOmniziaIdExists(omniziaId))
      throw new CustomException("Wrong ID", "Your provided omnizia id is wrong");
    List<Publications> publications = repository.findAllByHcpViqId(omniziaId);
    return PublicationsMapper.mapToPublicationsDto(publications);
  }
}
