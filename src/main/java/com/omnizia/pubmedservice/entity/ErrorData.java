package com.omnizia.pubmedservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "error_data")
public class ErrorData {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private UUID id;

  @JsonProperty("job_id")
  @Column(name = "job_id")
  private UUID jobId;

  @JsonProperty("hcp_viq_id")
  @Column(name = "hcp_viq_id")
  private String hcpViqId;

  @JsonProperty("job_title")
  @Column(name = "job_title")
  private String jobTitle;

  @JsonProperty("timestamp")
  @Column(name = "timestamp")
  private OffsetDateTime timestamp;
}
