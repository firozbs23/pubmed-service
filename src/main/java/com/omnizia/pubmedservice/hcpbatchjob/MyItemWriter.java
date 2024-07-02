package com.omnizia.pubmedservice.hcpbatchjob;

import java.util.List;

import com.omnizia.pubmedservice.entity.PubmedData;
import com.omnizia.pubmedservice.service.JobDataService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

@Slf4j
@Scope("step")
public class MyItemWriter implements ItemWriter<List<PubmedData>> {
  private final JobDataService jobDataService;

  public MyItemWriter(
      @Value("#{jobParameters['jobId']}") String jobId, JobDataService jobDataService) {
    log.info("Job id inside write: {}", jobId);
    this.jobDataService = jobDataService;
  }

  @Override
  public void write(@NonNull Chunk<? extends List<PubmedData>> chunk) {
    for (int i = 0; i < chunk.getItems().size(); i++) {
      List<PubmedData> jobDataList = chunk.getItems().get(i);
      jobDataService.saveJobDataList(jobDataList);
    }
  }
}
