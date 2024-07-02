package com.omnizia.pubmedservice.hcpbatchjob;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;

@Slf4j
@Scope("step")
public class MyItemWriter implements ItemWriter<String> {

  public MyItemWriter(@Value("#{jobParameters['jobId']}") String jobId) {
    log.info("Job id inside write: {}", jobId);
  }

  @Override
  public void write(@NonNull Chunk<? extends String> chunk) {
    List<String> omniziaIds = new ArrayList<>(chunk.getItems());
    log.info("Chunk size : {}", omniziaIds.size());

    // TODO:
    log.info("Current Thread Name: {}", Thread.currentThread());

    log.info("Chunk size after status changed : {}", omniziaIds.size());
    log.info("Chunk data saved.");
  }
}
