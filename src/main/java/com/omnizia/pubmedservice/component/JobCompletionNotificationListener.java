package com.omnizia.pubmedservice.component;

import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.service.JobStatusService;
import com.omnizia.pubmedservice.constant.BatchJobConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.omnizia.pubmedservice.constant.BatchJobConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobCompletionNotificationListener implements JobExecutionListener {

  private final JobStatusService jobStatusService;

  @Override
  public void beforeJob(JobExecution jobExecution) {
    String jobId = jobExecution.getJobParameters().getString(BatchJobConstants.JOB_ID);
    log.info("Job execution started. JobId: {}", jobId);
  }

  @Override
  public void afterJob(JobExecution jobExecution) {
    String jobId = jobExecution.getJobParameters().getString(BatchJobConstants.JOB_ID);
    String updatedStatus = JOB_STATUS_UNKNOWN;
    JobStatusDto jobStatus = null;
    if (jobId != null) jobStatus = jobStatusService.getJobStatusDto(UUID.fromString(jobId));

    if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("Job completed. JobId: {}", jobId);
      updatedStatus = JOB_STATUS_FINISHED;
    } else if (jobExecution.getStatus() == BatchStatus.FAILED) {
      log.info("Job failed. JobId: {}", jobId);
      updatedStatus = JOB_STATUS_FAILED;
    }

    if (jobStatus != null) {
      jobStatus.setJobStatus(updatedStatus);
      jobStatus.setTimestamp(OffsetDateTime.now());
      jobStatusService.updateJobStatus(jobStatus);
      log.info("Job status updated. JobId: {}", jobId);
    }
  }
}
