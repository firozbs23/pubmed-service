package com.omnizia.pubmedservice.repository;

import java.util.UUID;
import java.util.List;

import com.omnizia.pubmedservice.entity.PubmedData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PubmedDataRepository extends JpaRepository<PubmedData, UUID> {
  List<PubmedData> findByJobId(UUID jobId);
}
