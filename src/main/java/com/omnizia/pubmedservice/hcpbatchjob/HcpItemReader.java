package com.omnizia.pubmedservice.hcpbatchjob;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.omnizia.pubmedservice.service.JobDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

@Slf4j
public class HcpItemReader implements ItemReader<String> {

  private Iterator<String> dataIterator;
  private final List<String> omniziaIds;

  public HcpItemReader(String jobId, JobDataService jobDataService) {
    log.info("Creating HCP item reader for jobId: {}", jobId);
    omniziaIds = jobDataService.getAllHcpViqIdsByJobId(UUID.fromString(jobId));
  }

  @Override
  public String read() {
    if (dataIterator == null) {
      if (omniziaIds != null) {
        dataIterator = omniziaIds.iterator();
        log.info("Read item size : {}", omniziaIds.size());
        log.info("Current thead: {}", Thread.currentThread());
      } else log.info("Not able to read item.");
    }

    if (dataIterator.hasNext()) {
      return dataIterator.next();
    } else {
      return null;
    }
  }
}
