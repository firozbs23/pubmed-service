package com.omnizia.pubmedservice.hcpbatchjob;

import com.omnizia.pubmedservice.entity.JobData;
import com.omnizia.pubmedservice.entity.PubmedData;
import com.omnizia.pubmedservice.mapper.PubmedDataMapper;
import com.omnizia.pubmedservice.service.PubmedRestService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.*;

@Slf4j
public class HcpItemProcessor implements ItemProcessor<JobData, List<PubmedData>> {

  private final PubmedRestService restService;

  public HcpItemProcessor(PubmedRestService restService) {
    this.restService = restService;
  }

  @Override
  public List<PubmedData> process(@NonNull JobData jobData) {
    log.info("Item omnizia_id : {}", jobData.getHcpViqId());
    String threadName = Thread.currentThread().toString();
    List<PubmedData> processedData = processJobData(jobData);
    log.info("Current Thread Name: {}", threadName);
    return processedData;
  }

  private List<PubmedData> processJobData(JobData jobData) {
    PubmedData[] pubmedData = restService.getPubmedDataList(jobData);
    List<PubmedData> pubmedDataList = new ArrayList<>();
    for (PubmedData pubmed : pubmedData) {
      pubmedDataList.add(PubmedDataMapper.mapToPubmedData(pubmed, jobData));
    }
    return pubmedDataList;
  }
}
