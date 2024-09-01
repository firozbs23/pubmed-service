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
@Table(name = "job_data")
public class JobData {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private UUID id;

  @JsonProperty("job_id")
  @Column(name = "job_id")
  private UUID jobId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "job_id",
      referencedColumnName = "job_id",
      insertable = false,
      updatable = false)
  private JobStatus jobStatus;

  @JsonProperty("hcp_viq_id")
  @Column(name = "hcp_viq_id")
  private String hcpViqId;

  @JsonProperty("job_title")
  @Column(name = "job_title")
  private String jobTitle;

  @JsonProperty("matching_external_id")
  @Column(name = "matching_external_id")
  private String matchingExternalId;

  @JsonProperty("timestamp")
  @Column(name = "timestamp")
  private OffsetDateTime timestamp;
}
