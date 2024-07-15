package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.entity.JobStatus;
import com.omnizia.pubmedservice.mapper.JobStatusMapper;
import com.omnizia.pubmedservice.repository.JobStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobStatusService {

  private final JobStatusRepository jobStatusRepository;

  public JobStatusDto getJobStatus(UUID jobId) {
    JobStatus jobStatus = jobStatusRepository.findById(jobId).orElse(null);
    if (jobStatus == null) return null;
    return JobStatusMapper.mapToJobStatusDto(jobStatus);
  }

  public JobStatusDto saveJobStatus(JobStatusDto jobStatusDto) {
    JobStatus newJobStatus = JobStatusMapper.mapToJobStatus(jobStatusDto);
    JobStatus savedJobStatus = jobStatusRepository.save(newJobStatus);
    return JobStatusMapper.mapToJobStatusDto(savedJobStatus);
  }
}
