package com.omnizia.pubmedservice.hcpbatchjob;

import com.omnizia.pubmedservice.entity.JobData;
import com.omnizia.pubmedservice.entity.PubmedData;
import com.omnizia.pubmedservice.service.PubmedRestService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.time.OffsetDateTime;
import java.util.*;

@Slf4j
public class HcpItemProcessor implements ItemProcessor<JobData, List<PubmedData>> {

  private final PubmedRestService restService;

  public HcpItemProcessor(PubmedRestService restService) {
    this.restService = restService;
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
    PubmedData[] pubmedData = restService.getPubmedDataList(jobData);
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
