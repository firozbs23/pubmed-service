package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.constant.DefaultConstants;
import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.dto.UudidDto;
import com.omnizia.pubmedservice.entity.ErrorData;
import com.omnizia.pubmedservice.entity.JobStatus;
import com.omnizia.pubmedservice.entity.PubmedData;
import com.omnizia.pubmedservice.exception.CustomException;
import com.omnizia.pubmedservice.mapper.JobStatusMapper;
import com.omnizia.pubmedservice.constant.DbSelectorConstants;
import com.omnizia.pubmedservice.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

import static com.omnizia.pubmedservice.constant.BatchJobConstants.*;
import static com.omnizia.pubmedservice.constant.DefaultConstants.UNKNOWN;

@Slf4j
@Service
@RequiredArgsConstructor
public class PubmedService {

  private final UudidService uudidService;
  private final PubmedRestService restService;
  private final JobLauncherService jobLauncherService;
  private final JobStatusService jobStatusService;
  private final PubmedDataService pubmedDataService;
  private final HcpService hcpService;

  public JobStatusDto startBatchJob(List<String> omniziaIds, String jobTitle) {
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

  public JobStatusDto getPubmedData(List<String> publicationIds, String jobTitle) {
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
                List<ErrorData> errorDataList = new ArrayList<>();
                for (String omniziaId : omniziaIds) {
                  omniziaId = omniziaId == null ? StringUtils.EMPTY : omniziaId.trim();
                  try {
                    if (hcpService.checkOmniziaIdExists(omniziaId)) {
                      List<UudidDto> uudidDtos = uudidService.getUudidsByOmniziaId(omniziaId);
                      uudidList.addAll(uudidDtos);
                    } else {
                      // If omniziaId is wrong, add to error list
                      errorDataList.add(
                          ErrorData.builder()
                              .jobId(uuid)
                              .hcpViqId(omniziaId)
                              .jobTitle(jobTitle)
                              .message("Wrong omnizia id")
                              .timestamp(OffsetDateTime.now())
                              .build());
                    }
                  } catch (JDBCConnectionException e) {
                    // Not able to connect database
                    errorDataList.add(
                        ErrorData.builder()
                            .jobId(uuid)
                            .hcpViqId(omniziaId)
                            .jobTitle(jobTitle)
                            .message(e.getMessage())
                            .timestamp(OffsetDateTime.now())
                            .build());

                    log.error(e.getMessage(), e);
                  }
                }

                jobLauncherService.runJob(uuid, uudidList, jobTitle, errorDataList);
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

  public JobStatusDto processIdAndStartBatchJob(String omniziaId, String title) {
    try {
      DataSourceContextHolder.setDataSourceType(DbSelectorConstants.OLAM);
      if (StringUtils.isBlank(omniziaId) || !hcpService.checkOmniziaIdExists(omniziaId)) {
        throw new CustomException(
            HttpStatus.BAD_REQUEST.name(), "Your provided omnizia_id is wrong or does not exist");
      }

      List<String> omniziaIds = List.of(omniziaId.trim());
      String jobTitle = StringUtils.getStringOrDefault(title, UNKNOWN);
      return startBatchJob(omniziaIds, jobTitle);
    } finally {
      DataSourceContextHolder.clearDataSourceType();
    }
  }

  public JobStatusDto getPubmedDataByPmid(String pubmedId, String jobTitle) {
    try {
      List<String> pubmedIds = List.of(pubmedId);
      return getPubmedData(pubmedIds, jobTitle.trim());
    } catch (IllegalArgumentException e) {
      throw new CustomException("Invalid File", e.getMessage());
    }
  }
}
