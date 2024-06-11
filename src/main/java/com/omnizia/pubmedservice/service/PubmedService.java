package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.util.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PubmedService {

  public JobStatusDto startPubmedJob(List<String> omniziaIds, String jobTitle) {
    UUID uuid = UUID.randomUUID();
    OffsetDateTime dateTime = OffsetDateTime.now();

    return JobStatusDto.builder()
        .jobId(uuid)
        .jobStatus(JobStatus.RUNNING)
        .jobTitle(jobTitle)
        .timestamp(dateTime)
        .build();
  }
}
