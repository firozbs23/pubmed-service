package com.omnizia.pubmedservice.repository;

import com.omnizia.pubmedservice.entity.JobData;

import java.util.List;
import java.util.UUID;

import com.omnizia.pubmedservice.entity.PubmedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDataRepository extends JpaRepository<JobData, UUID> {

  List<JobData> findByJobId(UUID jobId);

  @Query("SELECT jd.hcpViqId FROM JobData jd WHERE jd.jobId = :jobId")
  List<String> findAllHcpViqIdByJobId(@Param("jobId") UUID jobId);
}
