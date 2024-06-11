package com.omnizia.pubmedservice.config;

import com.omnizia.pubmedservice.batchjob.MyItemProcessor;
import com.omnizia.pubmedservice.batchjob.MyItemReader;
import com.omnizia.pubmedservice.batchjob.MyItemWriter;
import com.omnizia.pubmedservice.dto.TestDto;
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

  @Bean
  public Job processJob(Step step) {
    return new JobBuilder("processJob", jobRepository).start(step).build();
  }

  @Bean
  public Step step(
      ItemReader<TestDto> reader,
      ItemProcessor<TestDto, TestDto> processor,
      ItemWriter<TestDto> writer) {
    return new StepBuilder("step", jobRepository)
        .<TestDto, TestDto>chunk(10, platformTransactionManager)
        .reader(reader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  @Bean
  @StepScope
  public ItemReader<TestDto> reader() {
    return new MyItemReader();
  }

  @Bean
  public ItemProcessor<TestDto, TestDto> processor() {
    return new MyItemProcessor();
  }

  @Bean
  @StepScope
  public ItemWriter<TestDto> writer(@Value("#{jobParameters['jobId']}") String jobId) {
    return new MyItemWriter(jobId);
  }
}
