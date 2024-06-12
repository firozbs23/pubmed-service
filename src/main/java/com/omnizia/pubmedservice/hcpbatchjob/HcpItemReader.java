package com.omnizia.pubmedservice.hcpbatchjob;

import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;

@Slf4j
public class HcpItemReader implements ItemReader<String> {

  private Iterator<String> dataIterator;
  private final List<String> omniziaIds;

  public HcpItemReader(String jobId, String data, ObjectMapper mapper) {
    log.info("Creating HCP item reader for jobId: {}", jobId);
    try {
      this.omniziaIds = mapper.readValue(data, new TypeReference<>() {});
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String read() {
    if (dataIterator == null) {
      if (omniziaIds != null) {
        dataIterator = omniziaIds.iterator();
        log.info("Read item size : {}", omniziaIds.size());
      } else log.info("Not able to read item.");
    }

    if (dataIterator.hasNext()) {
      return dataIterator.next();
    } else {
      return null;
    }
  }
}
