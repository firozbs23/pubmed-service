package com.omnizia.pubmedservice.hcpbatchjob;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.omnizia.pubmedservice.entity.JobData;
import com.omnizia.pubmedservice.service.JobDataService1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

@Slf4j
public class HcpItemReader implements ItemReader<JobData> {

  private Iterator<JobData> dataIterator;
  private final List<JobData> jobDataList;

  public HcpItemReader(String jobId, JobDataService1 jobDataService) {
    log.info("Creating HCP item reader for jobId: {}", jobId);
    jobDataList = jobDataService.getPubmedJobDataList(UUID.fromString(jobId));
  }

  @Override
  public JobData read() {
    if (dataIterator == null) {
      if (jobDataList != null) {
        dataIterator = jobDataList.iterator();
        log.info("Read item size : {}", jobDataList.size());
      } else log.info("Not able to read item.");
    }

    if (dataIterator.hasNext()) {
      return dataIterator.next();
    } else {
      return null;
    }
  }
}
