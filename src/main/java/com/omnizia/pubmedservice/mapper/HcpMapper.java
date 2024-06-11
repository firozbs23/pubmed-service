package com.omnizia.pubmedservice.mapper;

import com.omnizia.pubmedservice.dto.HcpDto;
import com.omnizia.pubmedservice.entity.Hcp;

import java.util.List;

public class HcpMapper {

  public static List<HcpDto> mapToHcpDto(List<Hcp> hcps) {
    return hcps.stream()
        .map(
            hcp ->
                HcpDto.builder()
                    .omniziaId(hcp.getViquiaId())
                    .firstName(hcp.getMiddleName())
                    .middleName(hcp.getMiddleName())
                    .lastName(hcp.getLastName())
                    .countryIso2(hcp.getCountryIso2())
                    .specialtyCode(hcp.getSpecialtyCode())
                    .indicationCity(hcp.getIndicationCity())
                    .nationalId(hcp.getNationalId())
                    .build())
        .toList();
  }
}
