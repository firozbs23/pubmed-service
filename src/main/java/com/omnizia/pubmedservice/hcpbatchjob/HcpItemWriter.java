package com.omnizia.pubmedservice.hcpbatchjob;

import java.util.List;

import com.omnizia.pubmedservice.entity.PubmedData;
import com.omnizia.pubmedservice.service.PubmedDataService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

@Slf4j
@Scope("step")
public class HcpItemWriter implements ItemWriter<List<PubmedData>> {

  private final PubmedDataService pubmedDataService;

  public HcpItemWriter(
      @Value("#{jobParameters['jobId']}") String jobId, PubmedDataService jobDataService) {
    log.info("Job id inside write: {}", jobId);
    this.pubmedDataService = jobDataService;
  }

  @Override
  public void write(@NonNull Chunk<? extends List<PubmedData>> chunk) {
    for (int i = 0; i < chunk.getItems().size(); i++) {
      List<PubmedData> jobDataList = chunk.getItems().get(i);
      pubmedDataService.savePubmedDataList(jobDataList);
    }
  }
}
