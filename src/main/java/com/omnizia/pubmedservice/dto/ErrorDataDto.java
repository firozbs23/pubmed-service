package com.omnizia.pubmedservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDataDto {

  @JsonProperty("job_id")
  private UUID jobId;

  @JsonProperty("hcp_viq_id")
  private String hcpViqId;

  @JsonProperty("message")
  private String message;

  @JsonProperty("job_title")
  private String jobTitle;

  @JsonProperty("timestamp")
  private String timestamp;
}
