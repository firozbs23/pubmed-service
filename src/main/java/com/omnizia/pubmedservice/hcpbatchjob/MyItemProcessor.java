package com.omnizia.pubmedservice.hcpbatchjob;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class MyItemProcessor implements ItemProcessor<String, String> {

  @Override
  public String process(@NonNull String omniziaId) {
    log.info("Item omnizia_id : {}", omniziaId);
    return omniziaId;
  }
}
