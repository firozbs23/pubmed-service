package com.omnizia.pubmedservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job_data")
public class JobData {

  @Id
  @Column(name = "id")
  private UUID id;

  @JsonProperty("job_id")
  @Column(
      name = "job_id",
      insertable = false,
      updatable = false) // Optional, to prevent changes to the job_id in JobData
  private UUID jobId;

  @JsonProperty("transaction_viq_id")
  @Column(name = "transaction_viq_id")
  private String transactionViqId;

  @JsonProperty("hcp_viq_id")
  @Column(name = "hcp_viq_id")
  private String hcpViqId;

  @JsonProperty("country_iso2")
  @Column(name = "country_iso2")
  private String countryIso2;

  @JsonProperty("specialty_code")
  @Column(name = "specialty_code")
  private String specialtyCode;

  @JsonProperty("publication_id")
  @Column(name = "publication_id")
  private String publicationId;

  @JsonProperty("title")
  @Column(name = "title")
  private String title;

  @JsonProperty("journal")
  @Column(name = "journal")
  private String journal;

  @JsonProperty("publication_date")
  @Column(name = "publication_date")
  private String publicationDate;

  @JsonProperty("abstract")
  @Column(name = "abstract")
  private String abstractValue;

  @JsonProperty("hcp_role")
  @Column(name = "hcp_role")
  private String hcpRole;

  @JsonProperty("publication_type")
  @Column(name = "publication_type")
  private String publicationType;

  @JsonProperty("issn")
  @Column(name = "issn")
  private String issn;

  @JsonProperty("url")
  @Column(name = "url")
  private String url;

  @JsonProperty("gds_tag_viq_id")
  @Column(name = "gds_tag_viq_id")
  private String gdsTagViqId;

  @JsonProperty("hcp_role_viq_id")
  @Column(name = "hcp_role_viq_id")
  private String hcpRoleViqId;

  @JsonProperty("key")
  @Column(name = "key")
  private Long key;

  @JsonProperty("created_by_job")
  @Column(name = "created_by_job")
  private String createdByJob;

  @JsonProperty("updated_by_job")
  @Column(name = "updated_by_job")
  private String updatedByJob;

  @JsonProperty("created_at")
  @Column(name = "created_at")
  private String createdAt;

  @JsonProperty("updated_at")
  @Column(name = "updated_at")
  private String updatedAt;
}
