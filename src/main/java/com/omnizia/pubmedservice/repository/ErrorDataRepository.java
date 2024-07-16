package com.omnizia.pubmedservice.repository;

import com.omnizia.pubmedservice.entity.ErrorData;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorDataRepository extends JpaRepository<ErrorData, UUID> {

  List<ErrorData> findByJobId(UUID jobId);

  @Query("SELECT ed.hcpViqId FROM ErrorData ed WHERE ed.jobId = :jobId")
  List<String> findAllHcpViqIdByJobId(@Param("jobId") UUID jobId);
}
