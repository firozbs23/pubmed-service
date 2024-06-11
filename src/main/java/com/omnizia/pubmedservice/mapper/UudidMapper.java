package com.omnizia.pubmedservice.mapper;

import com.omnizia.pubmedservice.dto.UudidDto;
import com.omnizia.pubmedservice.entity.Uudid;

import java.util.List;

public class UudidMapper {
  public static List<UudidDto> mapToUudidDto(List<Uudid> uudids) {
    return uudids.stream()
        .map(
            uudid ->
                UudidDto.builder()
                    .uuid(uudid.getUuid())
                    .type(uudid.getType())
                    .hcpId(uudid.getHcpId())
                    .ranking(uudid.getRanking())
                    .matchingExternalId(uudid.getMatchingExternalId())
                    .build())
        .toList();
  }
}
