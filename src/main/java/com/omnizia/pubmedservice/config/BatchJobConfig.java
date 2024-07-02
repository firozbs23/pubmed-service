package com.omnizia.pubmedservice.config;

import com.omnizia.pubmedservice.hcpbatchjob.MyItemProcessor;
import com.omnizia.pubmedservice.hcpbatchjob.HcpItemReader;
import com.omnizia.pubmedservice.hcpbatchjob.MyItemWriter;
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

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchJobConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final JobDataService jobDataService;

  @Bean
  public Job processJob(Step step) {
    return new JobBuilder("processJob", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      ItemReader<String> reader,
      ItemProcessor<String, String> processor,
      ItemWriter<String> writer) {
    return new StepBuilder("step", jobRepository)
        .<String, String>chunk(10, platformTransactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  @Bean
  @StepScope
  public ItemReader<String> reader(@Value("#{jobParameters['job_id']}") String jobId) {
    return new HcpItemReader(jobId, jobDataService);
  }

  @Bean
  public ItemProcessor<String, String> processor() {
    return new MyItemProcessor();
  }

  @Bean
  @StepScope
  public ItemWriter<String> writer(@Value("#{jobParameters['job_id']}") String jobId) {
    return new MyItemWriter(jobId);
  }
}
