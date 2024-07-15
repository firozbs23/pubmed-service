package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.dto.UudidDto;
import com.omnizia.pubmedservice.entity.JobData;
import com.omnizia.pubmedservice.entity.JobStatus;
import com.omnizia.pubmedservice.mapper.JobStatusMapper;
import com.omnizia.pubmedservice.repository.JobStatusRepository;
import com.omnizia.pubmedservice.constant.BatchJobConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
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

  @Transactional
  public void savePubmedJobStatus(UUID uuid, List<UudidDto> uudidDtos, String jobTitle) {
    JobStatus jobStatus = new JobStatus();
    jobStatus.setJobId(uuid);
    jobStatus.setJobTitle(jobTitle);
    jobStatus.setJobStatus(BatchJobConstants.JOB_STATUS_RUNNING);
    jobStatus.setTimestamp(OffsetDateTime.now());
    List<JobData> jobDataList = new ArrayList<>();

    for (UudidDto uudidDto : uudidDtos) {
      JobData jobData =
          JobData.builder()
              .jobId(uuid)
              .jobTitle(jobTitle)
              .hcpViqId(uudidDto.getHcpId())
              .matchingExternalId(uudidDto.getMatchingExternalId())
              .timestamp(OffsetDateTime.now())
              .build();
      jobDataList.add(jobData);
    }
    jobStatus.setJobDataList(jobDataList);
    repository.save(jobStatus);
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

  public void saveJobStatus(JobStatus jobStatus) {
    repository.save(jobStatus);
  }
}
