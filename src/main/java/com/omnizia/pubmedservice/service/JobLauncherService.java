package com.omnizia.pubmedservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.omnizia.pubmedservice.dbcontextholder.DataSourceContextHolder;
import com.omnizia.pubmedservice.util.DbSelectorUtils;
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

import static com.omnizia.pubmedservice.util.JobParamUtils.*;
import static com.omnizia.pubmedservice.util.JobParamUtils.DATA;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobLauncherService {

  private final JobLauncher jobLauncher;
  private final Job job;
  private final ObjectMapper mapper;

  public void runJob(UUID uuid, List<String> omniziaIds, String jobTitle) {
    Thread.ofVirtual()
        .start(
            () -> {
              try {
                DataSourceContextHolder.setDataSourceType(DbSelectorUtils.JOB_CONFIG);
                String selectedDb = DataSourceContextHolder.getDataSourceType();
                log.info("Running job with job_id: {} in DB: {}", uuid, selectedDb);
                JobParameters jobParameters =
                    new JobParametersBuilder()
                        .addDate(START_AT, new Date())
                        .addString(JOB_ID, uuid.toString())
                        .addString(JOB_TITLE, jobTitle)
                        .addString(DATA, mapper.writeValueAsString(omniziaIds))
                        .toJobParameters();
                jobLauncher.run(job, jobParameters);
              } catch (Exception e) {
                log.error("Error occurred while launching job with job_id: {}", uuid, e);
              } finally {
                log.info("Finished job with job_id: {}", uuid);
                DataSourceContextHolder.clearDataSourceType();
              }
            });
  }
}
