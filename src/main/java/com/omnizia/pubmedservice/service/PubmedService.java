package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.constant.DefaultConstants;
import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.dto.UudidDto;
import com.omnizia.pubmedservice.entity.JobStatus;
import com.omnizia.pubmedservice.entity.PubmedData;
import com.omnizia.pubmedservice.mapper.JobStatusMapper;
import com.omnizia.pubmedservice.constant.DbSelectorConstants;
import com.omnizia.pubmedservice.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

import static com.omnizia.pubmedservice.constant.BatchJobConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PubmedService {

  private final UudidService uudidService;
  private final PubmedRestService restService;
  private final JobLauncherService jobLauncherService;
  private final JobStatusService jobStatusService;
  private final PubmedDataService pubmedDataService;

  public JobStatusDto startPubmedBatchJob(List<String> omniziaIds, String jobTitle) {
    UUID uuid = UUID.randomUUID();
    OffsetDateTime dateTime = OffsetDateTime.now();
    jobTitle = jobTitle == null ? DefaultConstants.UNKNOWN : jobTitle.trim();

    startPubmedBatchJobInVirtualThread(omniziaIds, uuid, jobTitle);

    JobStatus jobStatus =
        JobStatus.builder()
            .jobId(uuid)
            .jobStatus(JOB_STATUS_RUNNING)
            .jobTitle(jobTitle)
            .timestamp(dateTime)
            .build();

    Thread.ofVirtual()
        .start(
            () -> {
              try {
                DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);
                jobStatusService.saveJobStatus(jobStatus);
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
            .jobStatus(JOB_STATUS_RUNNING)
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
                DataSourceContextHolder.setDataSourceType(DbSelectorConstants.OLAM);
                List<UudidDto> uudidList = new ArrayList<>();
                for (String omniziaId : omniziaIds) {
                  omniziaId = omniziaId == null ? StringUtils.EMPTY : omniziaId.trim();
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
                DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);
                JobStatus jobStatus = JobStatusMapper.mapToJobStatus(jobStatusDto);
                jobStatusService.saveJobStatus(jobStatus);

                try {
                  for (String pmid : publicationIds) {
                    PubmedData pubmedData = restService.getPubmedDataByPmid(pmid);
                    if (pubmedData != null) {
                      pubmedData.setJobId(jobStatusDto.getJobId());
                      pubmedData.setJobTitle(jobTitle);
                      pubmedData.setTimestamp(OffsetDateTime.now());
                      pubmedDataService.savePubmedData(pubmedData);
                      log.info("Pubmed data saved for PMID: {}", pmid);
                    } else {
                      log.error("Not able to save pubmed for pmid : {}", pmid);
                    }
                  }
                } catch (Exception e) {
                  jobStatus.setJobStatus(JOB_STATUS_FAILED);
                  jobStatusService.saveJobStatus(jobStatus);
                  log.error(e.getMessage());
                }

                jobStatus.setJobStatus(JOB_STATUS_FINISHED);
                jobStatusService.saveJobStatus(jobStatus);
                log.info("Finished pubmed data saving for JobId: {}", jobStatusDto.getJobId());
              } finally {
                DataSourceContextHolder.clearDataSourceType();
              }
            });
  }
}
