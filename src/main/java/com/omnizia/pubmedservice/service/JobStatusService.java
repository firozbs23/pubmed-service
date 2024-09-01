package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.entity.JobStatus;
import com.omnizia.pubmedservice.mapper.JobStatusMapper;
import com.omnizia.pubmedservice.repository.JobStatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class JobStatusService {

  private final JobStatusRepository repository;

  public JobStatusDto getJobStatusDto(UUID jobId) {
    JobStatus jobStatus = repository.findById(jobId).orElse(null);
    if (jobStatus == null) return null;
    return JobStatusMapper.mapToJobStatusDto(jobStatus);
  }

  public JobStatus getJobStatus(UUID jobId) {
    return repository.findById(jobId).orElse(null);
  }

  public JobStatusDto saveJobStatus(JobStatusDto jobStatusDto) {
    JobStatus newJobStatus = JobStatusMapper.mapToJobStatus(jobStatusDto);
    JobStatus savedJobStatus = repository.save(newJobStatus);
    return JobStatusMapper.mapToJobStatusDto(savedJobStatus);
  }

  public void updateJobStatus(JobStatusDto newJobStatus) {
    JobStatus updatedJobStatus =
        JobStatus.builder()
            .jobId(newJobStatus.getJobId())
            .jobStatus(newJobStatus.getJobStatus())
            .jobTitle(newJobStatus.getJobTitle())
            .timestamp(newJobStatus.getTimestamp())
            .build();
    repository.save(updatedJobStatus);
  }

  @Transactional
  public JobStatus saveJobStatus(JobStatus jobStatus) {
    Optional<JobStatus> existingJobStatus = repository.findById(jobStatus.getJobId());
    if (existingJobStatus.isPresent()) {
      JobStatus currentJobStatus = existingJobStatus.get();
      currentJobStatus.setJobStatus(jobStatus.getJobStatus());
      currentJobStatus.setJobDataList(jobStatus.getJobDataList());
      currentJobStatus.setTimestamp(jobStatus.getTimestamp());
      return repository.save(currentJobStatus);
    } else return repository.save(jobStatus);
  }
}
