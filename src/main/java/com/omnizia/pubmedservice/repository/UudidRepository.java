package com.omnizia.pubmedservice.repository;

import java.util.List;

import com.omnizia.pubmedservice.entity.Uudid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UudidRepository extends JpaRepository<Uudid, String> {

  List<Uudid> findAllByHcpId(String hcpId);
}
