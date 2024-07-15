package com.omnizia.pubmedservice.component;

import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.service.JobDataService1;
import com.omnizia.pubmedservice.util.JobParamUtils;
import com.omnizia.pubmedservice.util.JobStatusUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCompletionNotificationListener implements JobExecutionListener {

  private final JobDataService1 jobDataService;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    String jobId = jobExecution.getJobParameters().getString(JobParamUtils.JOB_ID);
    log.info("Job execution started. JobId: {}", jobId);
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    String jobId = jobExecution.getJobParameters().getString(JobParamUtils.JOB_ID);
    String updatedStatus = JobStatusUtils.UNKNOWN;
    JobStatusDto jobStatus = null;
    if (jobId != null) jobStatus = jobDataService.getPubmedJobStatus(UUID.fromString(jobId));

    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("Job completed. JobId: {}", jobId);
      updatedStatus = JobStatusUtils.FINISHED;
    } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
      log.info("Job failed. JobId: {}", jobId);
      updatedStatus = JobStatusUtils.FAILED;
    }

    if (jobStatus != null) {
      jobStatus.setJobStatus(updatedStatus);
      jobStatus.setTimestamp(OffsetDateTime.now());
      jobDataService.updateJobStatus(jobStatus);
      log.info("Job status updated. JobId: {}", jobId);
    }
  }
}
