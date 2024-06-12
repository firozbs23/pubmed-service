package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dto.JobStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static com.omnizia.pubmedservice.util.JobStatusUtils.RUNNING;

@Slf4j
@Service
@RequiredArgsConstructor
public class PubmedService {

  private final JobLauncherService jobLauncherService;

  public JobStatusDto startPubmedJob(List<String> omniziaIds, String jobTitle) {
    UUID uuid = UUID.randomUUID();
    OffsetDateTime dateTime = OffsetDateTime.now();
    jobLauncherService.runJob(uuid, omniziaIds, jobTitle);
    return JobStatusDto.builder()
        .jobId(uuid)
        .jobStatus(RUNNING)
        .jobTitle(jobTitle)
        .timestamp(dateTime)
        .build();
  }
}
