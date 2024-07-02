package com.omnizia.pubmedservice.hcpbatchjob;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class MyItemProcessor implements ItemProcessor<String, String> {

  @Override
  public String process(@NonNull String omniziaId) {
    log.info("Item omnizia_id : {}", omniziaId);
    String threadName = Thread.currentThread().toString();
    doIt();
    log.info("Current Thread Name: {}", threadName);
    return omniziaId;
  }

  private void doIt() {
    try {
      log.info("Before sleep. Thread: {}", Thread.currentThread());
      Thread.sleep(2000);
      log.info("After sleep. Thread: {}", Thread.currentThread());
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
