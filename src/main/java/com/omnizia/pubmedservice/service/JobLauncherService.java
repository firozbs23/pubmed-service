package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.UudidDto;
import com.omnizia.pubmedservice.constant.DbSelectorConstants;
import com.omnizia.pubmedservice.entity.ErrorData;
import com.omnizia.pubmedservice.entity.JobData;
import com.omnizia.pubmedservice.entity.JobStatus;
import com.omnizia.pubmedservice.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.omnizia.pubmedservice.constant.BatchJobConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobLauncherService {

  private final Job job;
  private final JobLauncher jobLauncher;
  private final JobStatusService jobStatusService;
  private final ErrorDataService errorDataService;

  public void runJob(
      UUID jobId, List<UudidDto> uudidDtos, String jobTitle, List<ErrorData> jobErrors) {
    Thread.ofVirtual()
        .start(
            () -> {
              try {
                DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);

                JobStatus jobStatus =
                    JobStatus.builder()
                        .jobId(jobId)
                        .jobStatus(JOB_STATUS_RUNNING)
                        .jobTitle(jobTitle)
                        .jobDataList(getJobDataList(uudidDtos, jobId, jobTitle))
                        .timestamp(OffsetDateTime.now())
                        .build();

                if (!jobErrors.isEmpty()) errorDataService.saveErrors(jobErrors);

                if (!uudidDtos.isEmpty()) {
                  jobStatusService.saveJobStatus(jobStatus);

                  JobParameters jobParameters =
                      new JobParametersBuilder()
                          .addDate(JOB_START_AT, new Date())
                          .addString(JOB_ID, StringUtils.getStringOrEmpty(jobId))
                          .addString(JOB_TITLE, jobTitle)
                          .toJobParameters();

                  jobLauncher.run(job, jobParameters);
                } else {
                  jobStatus.setJobStatus(JOB_STATUS_FINISHED);
                  jobStatusService.saveJobStatus(jobStatus);
                }

              } catch (Exception e) {
                log.error("Error occurred while launching job with job_id: {}", jobId, e);
              } finally {
                log.info("Finished job with job_id: {}", jobId);
                DataSourceContextHolder.clearDataSourceType();
              }
            });
  }

  private List<JobData> getJobDataList(List<UudidDto> uudidDtos, UUID jobId, String jobTitle) {
    List<JobData> jobDataList = new ArrayList<>();

    for (UudidDto uudidDto : uudidDtos) {
      JobData jobData =
          JobData.builder()
              .jobId(jobId)
              .jobTitle(jobTitle)
              .hcpViqId(uudidDto.getHcpId())
              .matchingExternalId(uudidDto.getMatchingExternalId())
              .timestamp(OffsetDateTime.now())
              .build();
      jobDataList.add(jobData);
    }

    return jobDataList;
  }
}
