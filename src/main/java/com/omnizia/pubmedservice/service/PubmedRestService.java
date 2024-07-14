package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.entity.JobData;
import com.omnizia.pubmedservice.entity.PubmedData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PubmedRestService {

  private final RestTemplate restTemplate;
  private static final String API_URL_PUBMED_BY_NAME = "http://localhost:5000/pubmed";
  private static final String API_URL_PUBMED_BY_PMID = "http://localhost:5000/pubmed/pmid";

  public PubmedData getPubmedDataByPmid(String pmid) {
    String url = API_URL_PUBMED_BY_PMID + "?pmid={pmid}";
    try {
      ResponseEntity<PubmedData> responseEntity =
          restTemplate.exchange(
              url,
              HttpMethod.GET,
              null,
              new ParameterizedTypeReference<>() {},
              Map.of("pmid", pmid));
      return responseEntity.getBody();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return null;
  }

  public PubmedData[] getPubmedDataList(JobData jobData) {
    try {
      String url = API_URL_PUBMED_BY_NAME + "?name={name}";
      ResponseEntity<PubmedData[]> responseEntity =
          restTemplate.exchange(
              url,
              HttpMethod.GET,
              null,
              new ParameterizedTypeReference<>() {},
              Map.of("name", jobData.getMatchingExternalId()));
      return responseEntity.getBody();
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return null;
  }
}
