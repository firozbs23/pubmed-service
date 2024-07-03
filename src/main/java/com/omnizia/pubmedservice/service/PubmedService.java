package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.dto.UudidDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.omnizia.pubmedservice.util.JobStatusUtils.RUNNING;

@Slf4j
@Service
@RequiredArgsConstructor
public class PubmedService {

  private final UudidService uudidService;
  private final JobLauncherService jobLauncherService;

    public JobStatusDto startPubmedJob(List<String> omniziaIds, String jobTitle) {
    UUID uuid = UUID.randomUUID();
    OffsetDateTime dateTime = OffsetDateTime.now();
    List<UudidDto> uudidList = new ArrayList<>();

    for (String omniziaId : omniziaIds) {
      List<UudidDto> uudidDtos = uudidService.getUudidsByOmniziaId(omniziaId);
      uudidList.addAll(uudidDtos);
    }

    jobLauncherService.runJob(uuid, uudidList, jobTitle);
    return JobStatusDto.builder()
        .jobId(uuid)
        .jobStatus(RUNNING)
        .jobTitle(jobTitle)
        .timestamp(dateTime)
        .build();
  }
}
