package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.dto.UudidDto;
import com.omnizia.pubmedservice.entity.JobStatus;
import com.omnizia.pubmedservice.entity.PubmedData;
import com.omnizia.pubmedservice.mapper.JobStatusMapper;
import com.omnizia.pubmedservice.repository.JobStatusRepository;
import com.omnizia.pubmedservice.repository.PubmedDataRepository;
import com.omnizia.pubmedservice.util.DbSelectorUtils;
import com.omnizia.pubmedservice.util.JobStatusUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

import static com.omnizia.pubmedservice.util.JobStatusUtils.RUNNING;

@Slf4j
@Service
@RequiredArgsConstructor
public class PubmedService {

  private final UudidService uudidService;
  private final PubmedRestService restService;
  private final JobLauncherService jobLauncherService;
  private final JobStatusRepository jobStatusRepository;
  private final PubmedDataRepository pubmedDataRepository;

  public JobStatusDto startPubmedBatchJob(List<String> omniziaIds, String jobTitle) {
    UUID uuid = UUID.randomUUID();
    OffsetDateTime dateTime = OffsetDateTime.now();

    startPubmedBatchJobInVirtualThread(omniziaIds, uuid, jobTitle);

    JobStatus jobStatus =
        JobStatus.builder()
            .jobId(uuid)
            .jobStatus(RUNNING)
            .jobTitle(jobTitle)
            .timestamp(dateTime)
            .build();

    Thread.ofVirtual()
        .start(
            () -> {
              try {
                DataSourceContextHolder.setDataSourceType(DbSelectorUtils.JOB_CONFIG);
                jobStatusRepository.save(jobStatus);
              } finally {
                DataSourceContextHolder.clearDataSourceType();
              }
            });

    return JobStatusMapper.mapToJobStatusDto(jobStatus);
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

    findingPubmedDataByPmidInVirtualThread(publicationIds, jobTitle, jobStatusDto);
    return jobStatusDto;
  }

  private void startPubmedBatchJobInVirtualThread(
      List<String> omniziaIds, UUID uuid, String jobTitle) {
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
  }

  private void findingPubmedDataByPmidInVirtualThread(
      List<String> publicationIds, String jobTitle, JobStatusDto jobStatusDto) {
    Thread.ofVirtual()
        .start(
            () -> {
              try {
                DataSourceContextHolder.setDataSourceType(DbSelectorUtils.JOB_CONFIG);
                JobStatus jobStatus = JobStatusMapper.mapToJobStatus(jobStatusDto);
                jobStatusRepository.save(jobStatus);

                try {
                  for (String pmid : publicationIds) {
                    PubmedData pubmedData = restService.getPubmedDataByPmid(pmid);
                    if (pubmedData != null) {
                      pubmedData.setJobId(jobStatusDto.getJobId());
                      pubmedData.setJobTitle(jobTitle);
                      pubmedData.setTimestamp(OffsetDateTime.now());
                      pubmedDataRepository.save(pubmedData);
                      log.info("Pubmed data saved for PMID: {}", pmid);
                    } else {
                      log.error("Not able to save pubmed for pmid : {}", pmid);
                    }
                  }
                } catch (Exception e) {
                  jobStatus.setJobStatus(JobStatusUtils.FAILED);
                  jobStatusRepository.save(jobStatus);
                  log.error(e.getMessage());
                }

                jobStatus.setJobStatus(JobStatusUtils.FINISHED);
                jobStatusRepository.save(jobStatus);
                log.info("Finished pubmed data saving for JobId: {}", jobStatusDto.getJobId());
              } finally {
                DataSourceContextHolder.clearDataSourceType();
              }
            });
  }
}
