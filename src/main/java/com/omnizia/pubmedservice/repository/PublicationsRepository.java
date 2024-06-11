package com.omnizia.pubmedservice.repository;

import java.util.List;

import com.omnizia.pubmedservice.entity.Publications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationsRepository extends JpaRepository<Publications, String> {

  List<Publications> findAllByHcpViqId(String hcpViqId);
}
