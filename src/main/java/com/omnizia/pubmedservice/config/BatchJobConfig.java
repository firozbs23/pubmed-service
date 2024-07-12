package com.omnizia.pubmedservice.config;

import com.omnizia.pubmedservice.component.JobCompletionNotificationListener;
import com.omnizia.pubmedservice.entity.JobData;
import com.omnizia.pubmedservice.entity.PubmedData;
import com.omnizia.pubmedservice.hcpbatchjob.HcpItemProcessor;
import com.omnizia.pubmedservice.hcpbatchjob.HcpItemReader;
import com.omnizia.pubmedservice.hcpbatchjob.HcpItemWriter;
import com.omnizia.pubmedservice.service.JobDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchJobConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final JobDataService jobDataService;
  private final RestTemplate restTemplate;
  private final JobCompletionNotificationListener jobCompletionNotificationListener;

  @Bean
  public Job processJob(Step step) {
    return new JobBuilder("processJob", jobRepository)
        .start(step)
        .listener(jobCompletionNotificationListener)
        .build();
  }

  @Bean
  public Step step(
      ItemReader<JobData> reader,
      ItemProcessor<JobData, List<PubmedData>> processor,
      ItemWriter<List<PubmedData>> writer) {
    return new StepBuilder("step", jobRepository)
        .<JobData, List<PubmedData>>chunk(10, platformTransactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  @Bean
  @StepScope
  public ItemReader<JobData> reader(@Value("#{jobParameters['job_id']}") String jobId) {
    return new HcpItemReader(jobId, jobDataService);
  }

  @Bean
  public ItemProcessor<JobData, List<PubmedData>> processor() {
    return new HcpItemProcessor(restTemplate);
  }

  @Bean
  @StepScope
  public ItemWriter<List<PubmedData>> writer(@Value("#{jobParameters['job_id']}") String jobId) {
    return new HcpItemWriter(jobId, jobDataService);
  }
}
