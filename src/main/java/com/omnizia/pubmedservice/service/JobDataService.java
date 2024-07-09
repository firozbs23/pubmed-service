package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.dto.PubmedDto;
import com.omnizia.pubmedservice.dto.UudidDto;
import com.omnizia.pubmedservice.entity.JobData;
import com.omnizia.pubmedservice.entity.JobStatus;
import com.omnizia.pubmedservice.entity.PubmedData;
import com.omnizia.pubmedservice.mapper.PubmedMapper;
import com.omnizia.pubmedservice.repository.JobDataRepository;
import com.omnizia.pubmedservice.repository.JobStatusRepository;
import com.omnizia.pubmedservice.repository.PubmedDataRepository;
import com.omnizia.pubmedservice.util.JobStatusUtils;
import jakarta.persistence.EntityNotFoundException;
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
public class JobDataService {

  private final JobStatusRepository jobStatusRepository;
  private final PubmedDataRepository pubmedDataRepository;
  private final JobDataRepository jobDataRepository;

  public JobStatus updateJobStatus(UUID jobId, String newStatus, OffsetDateTime newTimestamp) {
    Optional<JobStatus> existingJobStatusOptional = jobStatusRepository.findById(jobId);
    if (existingJobStatusOptional.isPresent()) {
      JobStatus existingJobStatus = existingJobStatusOptional.get();

      // Update fields except for job_id and job_title
      existingJobStatus.setJobStatus(newStatus);
      existingJobStatus.setTimestamp(newTimestamp);

      return jobStatusRepository.save(existingJobStatus);
    } else {
      throw new EntityNotFoundException("JobStatus with id " + jobId + " not found");
    }
  }

  @Transactional
  public void saveJobData(UUID uuid, List<UudidDto> uudidDtos, String jobTitle) {
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

  public List<JobData> getAllJobDataByJobId(UUID uuid) {
    Optional<JobStatus> jobStatus = jobStatusRepository.findById(uuid);
    if (jobStatus.isPresent()) return jobStatus.get().getJobDataList();
    return List.of();
  }

  public void saveJobDataList(List<PubmedData> jobDataList) {
    pubmedDataRepository.saveAll(jobDataList);
  }

  public List<PubmedDto> getPubmedDataByJobId(UUID jobId) {
    List<PubmedData> pubmedData = pubmedDataRepository.findByJobId(jobId);
    return PubmedMapper.mapToPubmedDto(pubmedData);
  }
}
