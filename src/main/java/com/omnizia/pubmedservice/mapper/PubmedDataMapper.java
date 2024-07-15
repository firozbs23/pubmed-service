package com.omnizia.pubmedservice.mapper;

import com.omnizia.pubmedservice.dto.PubmedDataDto;
import com.omnizia.pubmedservice.entity.PubmedData;
import com.omnizia.pubmedservice.util.StringUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class PubmedDataMapper {

  public static List<PubmedDataDto> mapToPubmedDto(List<PubmedData> pubmedData) {
    return pubmedData.stream()
        .map(
            pubmed ->
                PubmedDataDto.builder()
                    .jobId(pubmed.getJobId().toString())
                    .firstName(pubmed.getFirstName())
                    .lastName(pubmed.getLastName())
                    .initials(pubmed.getInitials())
                    .fullName(pubmed.getFullName())
                    .doi(pubmed.getDoi())
                    .pmcid(pubmed.getPmcid())
                    .issn(pubmed.getIssn())
                    .journal(pubmed.getJournal())
                    .affiliations(pubmed.getAffiliations())
                    .abstractValue(pubmed.getAbstractValue())
                    .publicationId(pubmed.getPublicationId())
                    .publicationType(pubmed.getPublicationType())
                    .publicationDate(pubmed.getPublicationDate())
                    .meshTerms(pubmed.getMeshTerms())
                    .url(pubmed.getUrl())
                    .publicationPlatform(pubmed.getPublicationPlatform())
                    .hcpRole(pubmed.getHcpRole())
                    .searchName(pubmed.getSearchName())
                    .countryIso2(pubmed.getCountryIso2())
                    .key(pubmed.getKey())
                    .hcpViqId(pubmed.getHcpViqId())
                    .timestamp(StringUtils.getStringOrEmpty(pubmed.getTimestamp()))
                    .title(pubmed.getTitle())
                    .jobTitle(pubmed.getJobTitle())
                    .createdByJob(pubmed.getCreatedByJob())
                    .gdsTagViqId(pubmed.getGdsTagViqId())
                    .createdAt(pubmed.getCreatedAt())
                    .updatedAt(pubmed.getUpdatedAt())
                    .updatedByJob(pubmed.getUpdatedByJob())
                    .specialtyCode(pubmed.getSpecialtyCode())
                    .transactionViqId(pubmed.getTransactionViqId())
                    .build())
        .toList();
  }

  public static PubmedDataDto mapToPubmedDataDto(PubmedData pubmed) {
    return PubmedDataDto.builder()
        .jobId(pubmed.getJobId().toString())
        .firstName(pubmed.getFirstName())
        .lastName(pubmed.getLastName())
        .initials(pubmed.getInitials())
        .fullName(pubmed.getFullName())
        .doi(pubmed.getDoi())
        .pmcid(pubmed.getPmcid())
        .issn(pubmed.getIssn())
        .journal(pubmed.getJournal())
        .affiliations(pubmed.getAffiliations())
        .abstractValue(pubmed.getAbstractValue())
        .publicationId(pubmed.getPublicationId())
        .publicationType(pubmed.getPublicationType())
        .publicationDate(pubmed.getPublicationDate())
        .meshTerms(pubmed.getMeshTerms())
        .url(pubmed.getUrl())
        .publicationPlatform(pubmed.getPublicationPlatform())
        .hcpRole(pubmed.getHcpRole())
        .searchName(pubmed.getSearchName())
        .countryIso2(pubmed.getCountryIso2())
        .key(pubmed.getKey())
        .hcpViqId(pubmed.getHcpViqId())
        .timestamp(StringUtils.getStringOrEmpty(pubmed.getTimestamp()))
        .title(pubmed.getTitle())
        .jobTitle(pubmed.getJobTitle())
        .createdByJob(pubmed.getCreatedByJob())
        .gdsTagViqId(pubmed.getGdsTagViqId())
        .createdAt(pubmed.getCreatedAt())
        .updatedAt(pubmed.getUpdatedAt())
        .updatedByJob(pubmed.getUpdatedByJob())
        .specialtyCode(pubmed.getSpecialtyCode())
        .transactionViqId(pubmed.getTransactionViqId())
        .build();
  }

  public static PubmedData mapToPubmedData(PubmedDataDto pubmed) {
    return PubmedData.builder()
        .jobId(UUID.fromString(pubmed.getJobId()))
        .firstName(pubmed.getFirstName())
        .lastName(pubmed.getLastName())
        .initials(pubmed.getInitials())
        .fullName(pubmed.getFullName())
        .doi(pubmed.getDoi())
        .pmcid(pubmed.getPmcid())
        .issn(pubmed.getIssn())
        .journal(pubmed.getJournal())
        .affiliations(pubmed.getAffiliations())
        .abstractValue(pubmed.getAbstractValue())
        .publicationId(pubmed.getPublicationId())
        .publicationType(pubmed.getPublicationType())
        .publicationDate(pubmed.getPublicationDate())
        .meshTerms(pubmed.getMeshTerms())
        .url(pubmed.getUrl())
        .publicationPlatform(pubmed.getPublicationPlatform())
        .hcpRole(pubmed.getHcpRole())
        .searchName(pubmed.getSearchName())
        .countryIso2(pubmed.getCountryIso2())
        .key(pubmed.getKey())
        .hcpViqId(pubmed.getHcpViqId())
        .timestamp(OffsetDateTime.parse(pubmed.getTimestamp()))
        .title(pubmed.getTitle())
        .jobTitle(pubmed.getJobTitle())
        .createdByJob(pubmed.getCreatedByJob())
        .gdsTagViqId(pubmed.getGdsTagViqId())
        .createdAt(pubmed.getCreatedAt())
        .updatedAt(pubmed.getUpdatedAt())
        .updatedByJob(pubmed.getUpdatedByJob())
        .specialtyCode(pubmed.getSpecialtyCode())
        .transactionViqId(pubmed.getTransactionViqId())
        .build();
  }
}
