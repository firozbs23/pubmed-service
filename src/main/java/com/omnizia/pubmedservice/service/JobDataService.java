package com.omnizia.pubmedservice.service;

import com.omnizia.pubmedservice.entity.JobData;
import com.omnizia.pubmedservice.entity.JobStatus;
import com.omnizia.pubmedservice.repository.JobDataRepository;
import com.omnizia.pubmedservice.repository.JobStatusRepository;
import com.omnizia.pubmedservice.util.JobStatusUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
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

  private final JobDataRepository jobDataRepository;
  private final JobStatusRepository jobStatusRepository;

  public JobData updateJobData(UUID id, JobData updatedJobData) {
    Optional<JobData> existingJobDataOptional = jobDataRepository.findById(id);
    if (existingJobDataOptional.isPresent()) {
      JobData existingJobData = existingJobDataOptional.get();

      // Update fields except for id and job_id
      existingJobData.setTransactionViqId(updatedJobData.getTransactionViqId());
      existingJobData.setHcpViqId(updatedJobData.getHcpViqId());
      existingJobData.setCountryIso2(updatedJobData.getCountryIso2());
      existingJobData.setSpecialtyCode(updatedJobData.getSpecialtyCode());
      existingJobData.setPublicationId(updatedJobData.getPublicationId());
      existingJobData.setTitle(updatedJobData.getTitle());
      existingJobData.setJournal(updatedJobData.getJournal());
      existingJobData.setPublicationDate(updatedJobData.getPublicationDate());
      existingJobData.setAbstractValue(updatedJobData.getAbstractValue());
      existingJobData.setHcpRole(updatedJobData.getHcpRole());
      existingJobData.setPublicationType(updatedJobData.getPublicationType());
      existingJobData.setIssn(updatedJobData.getIssn());
      existingJobData.setUrl(updatedJobData.getUrl());
      existingJobData.setGdsTagViqId(updatedJobData.getGdsTagViqId());
      existingJobData.setHcpRoleViqId(updatedJobData.getHcpRoleViqId());
      existingJobData.setKey(updatedJobData.getKey());
      existingJobData.setCreatedByJob(updatedJobData.getCreatedByJob());
      existingJobData.setUpdatedByJob(updatedJobData.getUpdatedByJob());
      existingJobData.setCreatedAt(updatedJobData.getCreatedAt());
      existingJobData.setUpdatedAt(updatedJobData.getUpdatedAt());

      return jobDataRepository.save(existingJobData);
    } else {
      throw new EntityNotFoundException("JobData with id " + id + " not found");
    }
  }

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

  public List<String> getAllHcpViqIdsByJobId(UUID jobId) {
    return jobDataRepository.findAllHcpViqIdByJobId(jobId);
  }

  @Transactional
  public void saveOmniziaIds(UUID uuid, List<String> omniziaIds, String jobTitle) {

    JobStatus jobStatus = new JobStatus();
    jobStatus.setJobId(uuid);
    jobStatus.setJobTitle(jobTitle);
    jobStatus.setJobStatus(JobStatusUtils.RUNNING);
    List<JobData> jobDataList = new ArrayList<>();

    for (String omniziaId : omniziaIds) {
      jobDataList.add(
          JobData.builder().id(UUID.randomUUID()).jobId(uuid).hcpViqId(omniziaId).build());
    }
    jobStatus.setJobDataList(jobDataList);

    jobStatusRepository.save(jobStatus);
  }
}
