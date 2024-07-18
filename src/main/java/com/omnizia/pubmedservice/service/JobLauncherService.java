package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.dto.UudidDto;
import com.omnizia.pubmedservice.constant.DbSelectorConstants;
import com.omnizia.pubmedservice.entity.ErrorData;
import com.omnizia.pubmedservice.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

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
      UUID jobId, List<UudidDto> uudidDtos, String jobTitle, List<ErrorData> errorDataList) {
    Thread.ofVirtual()
        .start(
            () -> {
              try {
                DataSourceContextHolder.setDataSourceType(DbSelectorConstants.JOB_CONFIG);

                jobStatusService.savePubmedJobStatus(jobId, uudidDtos, jobTitle);
                errorDataService.saveErrorDataList(errorDataList);

                JobParameters jobParameters =
                    new JobParametersBuilder()
                        .addDate(JOB_START_AT, new Date())
                        .addString(JOB_ID, StringUtils.getStringOrEmpty(jobId))
                        .addString(JOB_TITLE, jobTitle)
                        .toJobParameters();
                jobLauncher.run(job, jobParameters);
              } catch (Exception e) {
                log.error("Error occurred while launching job with job_id: {}", jobId, e);
              } finally {
                log.info("Finished job with job_id: {}", jobId);
                DataSourceContextHolder.clearDataSourceType();
              }
            });
  }
}
