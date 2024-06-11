package com.omnizia.pubmedservice.batchjob;

import com.omnizia.pubmedservice.dto.TestDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class MyItemProcessor implements ItemProcessor<TestDto, TestDto> {

  private final String className;

  public MyItemProcessor() {
    this.className = this.getClass().getSimpleName();
  }

  @Override
  public TestDto process(@NonNull TestDto item) {
    log.info("{}: Item Name : {}", className, item.getTitle());
    return item;
  }
}
