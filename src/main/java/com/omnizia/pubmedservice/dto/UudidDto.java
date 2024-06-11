package com.omnizia.pubmedservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UudidDto {

  @JsonProperty("uuid")
  private String uuid;

  @JsonProperty("type")
  private String type;

  @JsonProperty("matching_external_id")
  private String matchingExternalId;

  @JsonProperty("hcp_id")
  private String hcpId;

  @JsonProperty("ranking")
  private Integer ranking;
}
