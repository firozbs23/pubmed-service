package com.omnizia.pubmedservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job_status")
public class JobStatus {
  @Id
  @JsonProperty("job_id")
  @Column(name = "job_id")
  private UUID jobId;

  @JsonProperty("job_status")
  @Column(name = "job_status")
  private String jobStatus;

  @JsonProperty("job_title")
  @Column(name = "job_title")
  private String jobTitle;

  @JsonProperty("timestamp")
  @Column(name = "timestamp")
  private OffsetDateTime timestamp;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "job_id", referencedColumnName = "job_id")
  @JsonProperty("job_data_list")
  private List<JobData> jobDataList;
}
