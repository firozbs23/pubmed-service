package com.omnizia.pubmedservice.mapper;

import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.entity.JobStatus;

public class JobStatusMapper {
  public static JobStatus mapToJobStatus(JobStatusDto jobStatusDto) {
    return JobStatus.builder()
        .jobId(jobStatusDto.getJobId())
        .jobTitle(jobStatusDto.getJobTitle())
        .jobStatus(jobStatusDto.getJobStatus())
        .timestamp(jobStatusDto.getTimestamp())
        .build();
  }

  public static JobStatusDto mapToJobStatusDto(JobStatus jobStatus) {
    return JobStatusDto.builder()
        .jobId(jobStatus.getJobId())
        .jobTitle(jobStatus.getJobTitle())
        .jobStatus(jobStatus.getJobStatus())
        .timestamp(jobStatus.getTimestamp())
        .build();
  }
}
