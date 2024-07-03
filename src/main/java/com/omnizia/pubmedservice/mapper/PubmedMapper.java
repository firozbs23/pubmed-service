package com.omnizia.pubmedservice.mapper;

import com.omnizia.pubmedservice.dto.PubmedDto;
import com.omnizia.pubmedservice.entity.PubmedData;

import java.util.List;

public class PubmedMapper {
  public static List<PubmedDto> mapToPubmedDto(List<PubmedData> pubmedData) {
    return pubmedData.stream()
        .map(
            pubmed ->
                PubmedDto.builder()
                    .doi(pubmed.getDoi())
                    .affiliations(pubmed.getAffiliations())
                    .firstName(pubmed.getFirstName())
                    .countryIso2(pubmed.getCountryIso2())
                    .hcpRole(pubmed.getHcpRole())
                    .fullName(pubmed.getFullName())
                    .pmcid(pubmed.getPmcid())
                    .key(pubmed.getKey())
                    .abstractValue(pubmed.getAbstractValue())
                    .hcpViqId(pubmed.getHcpViqId())
                    .issn(pubmed.getIssn())
                    .initials(pubmed.getInitials())
                    .journal(pubmed.getJournal())
                    .lastName(pubmed.getLastName())
                    .meshTerms(pubmed.getMeshTerms())
                    .publicationDate(pubmed.getPublicationDate())
                    .url(pubmed.getUrl())
                    .publicationId(pubmed.getPublicationId())
                    .publicationType(pubmed.getPublicationType())
                    .searchName(pubmed.getSearchName())
                    .timestamp(pubmed.getTimestamp().toString())
                    .title(pubmed.getTitle())
                    .jobTitle(pubmed.getJobTitle())
                    .createdByJob(pubmed.getCreatedByJob())
                    .gdsTagViqId(pubmed.getGdsTagViqId())
                    .createdAt(pubmed.getCreatedAt())
                    .updatedAt(pubmed.getUpdatedAt())
                    .updatedByJob(pubmed.getUpdatedByJob())
                    .specialtyCode(pubmed.getSpecialtyCode())
                    .transactionViqId(pubmed.getTransactionViqId())
                    .jobId(pubmed.getJobId().toString())
                    .build())
        .toList();
  }
}
