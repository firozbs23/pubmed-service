package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.entity.JobData;
import com.omnizia.pubmedservice.repository.JobDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobDataService {

  private final JobDataRepository jobDataRepository;

  public JobData getJobData(UUID jobId) {
    return jobDataRepository.findById(jobId).orElse(null);
  }

  public JobData createJobData(JobData jobData) {
    return jobDataRepository.save(jobData);
  }
}
