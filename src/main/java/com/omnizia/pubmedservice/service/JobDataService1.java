package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dto.JobStatusDto;
import com.omnizia.pubmedservice.dto.PubmedDataDto;
import com.omnizia.pubmedservice.dto.UudidDto;
import com.omnizia.pubmedservice.entity.JobData;
import com.omnizia.pubmedservice.entity.JobStatus;
import com.omnizia.pubmedservice.entity.PubmedData;
import com.omnizia.pubmedservice.exception.CustomException;
import com.omnizia.pubmedservice.mapper.PubmedDataMapper;
import com.omnizia.pubmedservice.repository.JobStatusRepository;
import com.omnizia.pubmedservice.repository.PubmedDataRepository;
import com.omnizia.pubmedservice.util.JobStatusUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobDataService1 {

  private final JobStatusRepository jobStatusRepository;
  private final PubmedDataRepository pubmedDataRepository;

  @Transactional
  public void savePubmedJobStatus(UUID uuid, List<UudidDto> uudidDtos, String jobTitle) {
    JobStatus jobStatus = new JobStatus();
    jobStatus.setJobId(uuid);
    jobStatus.setJobTitle(jobTitle);
    jobStatus.setJobStatus(JobStatusUtils.RUNNING);
    jobStatus.setTimestamp(OffsetDateTime.now());
    List<JobData> jobDataList = new ArrayList<>();

    for (UudidDto uudidDto : uudidDtos) {
      jobDataList.add(
          JobData.builder()
              .jobId(uuid)
              .jobTitle(jobTitle)
              .hcpViqId(uudidDto.getHcpId())
              .matchingExternalId(uudidDto.getMatchingExternalId())
              .timestamp(OffsetDateTime.now())
              .build());
    }
    jobStatus.setJobDataList(jobDataList);
    jobStatusRepository.save(jobStatus);
  }

  public List<JobData> getPubmedJobDataList(UUID uuid) {
    Optional<JobStatus> jobStatus = jobStatusRepository.findById(uuid);
    if (jobStatus.isPresent()) return jobStatus.get().getJobDataList();
    return List.of();
  }

  public void saveJobDataList(List<PubmedData> jobDataList) {
    pubmedDataRepository.saveAll(jobDataList);
  }

  public List<PubmedDataDto> getPubmedDataByJobId(UUID jobId) {
    List<PubmedData> pubmedData = pubmedDataRepository.findByJobId(jobId);
    return PubmedDataMapper.mapToPubmedDto(pubmedData);
  }

  public JobStatusDto getPubmedJobStatus(UUID jobId) {
    JobStatus jobStatus = jobStatusRepository.findById(jobId).orElse(null);
    if (jobStatus == null) throw new CustomException("Not Found", "Not able to find job status");
    return JobStatusDto.builder()
        .jobId(jobStatus.getJobId())
        .jobStatus(jobStatus.getJobStatus())
        .jobTitle(jobStatus.getJobTitle())
        .timestamp(jobStatus.getTimestamp())
        .build();
  }

  public void updateJobStatus(JobStatusDto newJobStatus) {
    JobStatus updatedJobStatus =
        JobStatus.builder()
            .jobId(newJobStatus.getJobId())
            .jobStatus(newJobStatus.getJobStatus())
            .jobTitle(newJobStatus.getJobTitle())
            .timestamp(newJobStatus.getTimestamp())
            .build();
    jobStatusRepository.save(updatedJobStatus);
  }
}
