package com.omnizia.pubmedservice.hcpbatchjob;

import com.omnizia.pubmedservice.entity.JobData;
import com.omnizia.pubmedservice.entity.PubmedData;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
public class HcpItemProcessor implements ItemProcessor<JobData, List<PubmedData>> {
  private static final String BIO_PYTHON_API_URL = "http://localhost:5000/pubmed";
  private final RestTemplate restTemplate;

  public HcpItemProcessor(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<PubmedData> process(@NonNull JobData jobData) {
    log.info("Item omnizia_id : {}", jobData.getHcpViqId());
    String threadName = Thread.currentThread().toString();
    List<PubmedData> processedData = processJobData(jobData);
    log.info("Current Thread Name: {}", threadName);
    return processedData;
  }

  private List<PubmedData> processJobData(JobData jobData) {
    String url = BIO_PYTHON_API_URL + "?name={name}";
    ResponseEntity<PubmedData[]> responseEntity =
        restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<>() {},
            Map.of("name", jobData.getMatchingExternalId()));

    PubmedData[] pubmedData = Objects.requireNonNull(responseEntity.getBody());
    List<PubmedData> pubmedDataList = new ArrayList<>();
    for (PubmedData pubmed : pubmedData) {
      pubmedDataList.add(
          PubmedData.builder()
              .jobId(jobData.getJobId())
              .jobTitle(jobData.getJobTitle())
              .hcpViqId(jobData.getHcpViqId())
              .title(pubmed.getTitle())
              .hcpRole(pubmed.getHcpRole())
              .countryIso2(pubmed.getCountryIso2())
              .journal(pubmed.getJournal())
              .pmcid(pubmed.getPmcid())
              .doi(pubmed.getDoi())
              .abstractValue(pubmed.getAbstractValue())
              .affiliations(pubmed.getAffiliations())
              .firstName(pubmed.getFirstName())
              .lastName(pubmed.getLastName())
              .fullName(pubmed.getFullName())
              .initials(pubmed.getInitials())
              .matchingExternalId(jobData.getMatchingExternalId())
              .meshTerms(pubmed.getMeshTerms())
              .publicationId(pubmed.getPublicationId())
              .publicationType(pubmed.getPublicationType())
              .publicationDate(pubmed.getPublicationDate())
              .searchName(pubmed.getSearchName())
              .timestamp(OffsetDateTime.now())
              .publicationPlatform(pubmed.getPublicationPlatform())
              .issn(pubmed.getIssn())
              .url(pubmed.getUrl())
              .specialtyCode(pubmed.getSpecialtyCode())
              .createdByJob(pubmed.getCreatedByJob())
              .createdAt(pubmed.getCreatedAt())
              .updatedAt(pubmed.getUpdatedAt())
              .createdByJob(pubmed.getCreatedByJob())
              .build());
    }
    return pubmedDataList;
  }
}
