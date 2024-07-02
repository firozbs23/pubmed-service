package com.omnizia.pubmedservice.repository;

import java.util.UUID;

import com.omnizia.pubmedservice.entity.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobStatusRepository extends JpaRepository<JobStatus, UUID> {}
