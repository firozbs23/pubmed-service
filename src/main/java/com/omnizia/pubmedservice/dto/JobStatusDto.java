package com.omnizia.pubmedservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobStatusDto {

  @JsonProperty("job_id")
  private UUID jobId;

  @JsonProperty("job_status")
  private String jobStatus;

  @JsonProperty("job_title")
  private String jobTitle;

  @JsonProperty("timestamp")
  private OffsetDateTime timestamp;
}
