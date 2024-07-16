package com.omnizia.pubmedservice.repository;

import java.util.List;

import com.omnizia.pubmedservice.entity.Hcp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HcpRepository extends JpaRepository<Hcp, Long> {

  List<Hcp> findByCountryIso2IgnoreCase(String countryIso2);

  List<Hcp> findByCountryIso2IgnoreCaseAndSpecialtyCode(String countryIso2, String specialtyCode);

  Hcp findByViquiaId(String viquiaId);

  boolean existsByViquiaId(String viquiaId);
}
