package com.omnizia.pubmedservice.mapper;

import com.omnizia.pubmedservice.dto.PublicationsDto;
import com.omnizia.pubmedservice.entity.Publications;

import java.util.List;

public class PublicationsMapper {

  public static List<PublicationsDto> mapToPublicationsDto(List<Publications> publications) {
    return publications.stream()
        .map(
            publication ->
                PublicationsDto.builder()
                    .publicationId(publication.getPublicationId())
                    .title(publication.getTitle())
                    .abstractValue(publication.getAbstractValue())
                    .publicationDate(publication.getPublicationDate())
                    .publicationType(publication.getPublicationType())
                    .countryIso2(publication.getCountryIso2())
                    .issn(publication.getIssn())
                    .key(publication.getKey())
                    .specialtyCode(publication.getSpecialtyCode())
                    .gdsTagViqId(publication.getGdsTagViqId())
                    .hcpRole(publication.getHcpRole())
                    .hcpRoleViqId(publication.getHcpRoleViqId())
                    .createdAt(publication.getCreatedAt())
                    .createdByJob(publication.getCreatedByJob())
                    .url(publication.getUrl())
                    .hcpViqId(publication.getHcpViqId())
                    .journal(publication.getJournal())
                    .transactionViqId(publication.getTransactionViqId())
                    .updatedAt(publication.getUpdatedAt())
                    .updatedByJob(publication.getUpdatedByJob())
                    .build())
        .toList();
  }
}
