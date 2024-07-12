package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.dto.UudidDto;
import com.omnizia.pubmedservice.entity.JobStatus;
import com.omnizia.pubmedservice.entity.PubmedData;
import com.omnizia.pubmedservice.exception.CustomException;
import com.omnizia.pubmedservice.mapper.JobStatusMapper;
import com.omnizia.pubmedservice.repository.JobStatusRepository;
import com.omnizia.pubmedservice.repository.PubmedDataRepository;
import com.omnizia.pubmedservice.util.DbSelectorUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.*;

import static com.omnizia.pubmedservice.util.JobStatusUtils.RUNNING;

@Slf4j
@Service
@RequiredArgsConstructor
public class PubmedService {

  private static final String BIO_PYTHON_API_URL = "http://localhost:5000/pubmed/pmid";

  private final RestTemplate restTemplate;
  private final UudidService uudidService;
  private final JobLauncherService jobLauncherService;
  private final JobStatusRepository jobStatusRepository;
  private final PubmedDataRepository pubmedDataRepository;

  public JobStatusDto startPubmedJob(List<String> omniziaIds, String jobTitle) {
    UUID uuid = UUID.randomUUID();
    OffsetDateTime dateTime = OffsetDateTime.now();

    Thread.ofVirtual()
        .start(
            () -> {
              try {
                DataSourceContextHolder.setDataSourceType(DbSelectorUtils.OLAM);
                List<UudidDto> uudidList = new ArrayList<>();
                for (String omniziaId : omniziaIds) {
                  List<UudidDto> uudidDtos = uudidService.getUudidsByOmniziaId(omniziaId);
                  uudidList.addAll(uudidDtos);
                }

                jobLauncherService.runJob(uuid, uudidList, jobTitle);
              } finally {
                DataSourceContextHolder.clearDataSourceType();
              }
            });

    return JobStatusDto.builder()
        .jobId(uuid)
        .jobStatus(RUNNING)
        .jobTitle(jobTitle)
        .timestamp(dateTime)
        .build();
  }

  public void startFindingPubmedData(
      List<String> publicationIds, String jobTitle, JobStatusDto jobStatusDto) {
    Thread.ofVirtual()
        .start(
            () -> {
              try {
                DataSourceContextHolder.setDataSourceType(DbSelectorUtils.JOB_CONFIG);
                JobStatus jobStatus = JobStatusMapper.mapToJobStatus(jobStatusDto);
                jobStatusRepository.save(jobStatus);
                for (String pmid : publicationIds) {
                  String url = BIO_PYTHON_API_URL + "?pmid={pmid}";
                  try {
                    ResponseEntity<PubmedData> responseEntity =
                        restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {},
                            Map.of("pmid", pmid));

                    PubmedData pubmedData = Objects.requireNonNull(responseEntity.getBody());
                    pubmedData.setJobId(jobStatusDto.getJobId());
                    pubmedData.setJobTitle(jobTitle);
                    pubmedData.setTimestamp(OffsetDateTime.now());
                    pubmedDataRepository.save(pubmedData);
                    log.info("Pubmed data saved for PMID: {}", pmid);
                  } catch (Exception e) {
                    log.error("Not able to save pubmed for pmid : {}", pmid, e);
                  }
                }
                log.info("Finished pubmed data saving for JobId: {}", jobStatusDto.getJobId());
              } finally {
                DataSourceContextHolder.clearDataSourceType();
              }
            });
  }

  public JobStatusDto getPubmedJobStatus(UUID jobId) {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorUtils.JOB_CONFIG);
      JobStatus jobStatus = jobStatusRepository.findById(jobId).orElse(null);
      if (jobStatus == null) throw new CustomException("Not Found", "Not able to find job status");
      return JobStatusDto.builder()
          .jobId(jobStatus.getJobId())
          .jobStatus(jobStatus.getJobStatus())
          .jobTitle(jobStatus.getJobTitle())
          .timestamp(jobStatus.getTimestamp())
          .build();
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  public JobStatusDto findPubmedDataByPmid(List<String> publicationIds, String jobTitle) {
    UUID uuid = UUID.randomUUID();
    OffsetDateTime dateTime = OffsetDateTime.now();
    JobStatusDto jobStatusDto =
        JobStatusDto.builder()
            .jobId(uuid)
            .jobStatus(RUNNING)
            .jobTitle(jobTitle)
            .timestamp(dateTime)
            .build();

    startFindingPubmedData(publicationIds, jobTitle, jobStatusDto);
    return jobStatusDto;
  }
}
