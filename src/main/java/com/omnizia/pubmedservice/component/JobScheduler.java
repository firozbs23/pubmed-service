package com.omnizia.pubmedservice.component;

import java.util.Date;
import java.util.UUID;

import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobScheduler {

  private final JobLauncher jobLauncher;
  private final Job job;

  // Schedule job to run every day at 1 AM
  @Scheduled(cron = "0 0 1 * * ?")
  // @Scheduled(initialDelay = 0, fixedRate = Long.MAX_VALUE)
  public void runJob() {
    String currentTime = TimeUtils.getCurrentTimeUTC();
    String className = this.getClass().getSimpleName();
    try {
      DataSourceContextHolder.setDataSourceType("dataSource");
      log.info("{}: Running job {} in {}", className, job.getName(), currentTime);

      String jobId = UUID.randomUUID().toString();
      JobParameters jobParameters =
          new JobParametersBuilder()
              .addString("jobId", jobId)
              .addDate("startTime", new Date())
              .toJobParameters();
      jobLauncher.run(job, jobParameters);
    } catch (JobExecutionException e) {
      currentTime = TimeUtils.getCurrentTimeUTC();
      log.error("{}: Failed job {} in {}", className, job.getName(), currentTime, e);
    } finally {
      currentTime = TimeUtils.getCurrentTimeUTC();
      log.info("{}: Finished job {} in {}", className, job.getName(), currentTime);
      DataSourceContextHolder.clearDataSourceType();
    }
  }
}
