package com.omnizia.pubmedservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pubmed_data")
public class PubmedData {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private UUID id;

  @JsonProperty("job_id")
  @Column(name = "job_id")
  private UUID jobId;

  @JsonProperty("job_title")
  @Column(name = "job_title")
  private String jobTitle;

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
  private String key;

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

  @JsonProperty("matching_external_id")
  @Column(name = "matching_external_id")
  private String matchingExternalId;

  @JsonProperty("first_name")
  @Column(name = "first_name")
  private String firstName;

  @JsonProperty("last_name")
  @Column(name = "last_name")
  private String lastName;

  @JsonProperty("initials")
  @Column(name = "initials")
  private String initials;

  @JsonProperty("full_name")
  @Column(name = "full_name")
  private String fullName;

  @JsonProperty("affiliations")
  @Column(name = "affiliations")
  private String affiliations;

  @JsonProperty("pmcid")
  @Column(name = "pmcid")
  private String pmcid;

  @JsonProperty("doi")
  @Column(name = "doi")
  private String doi = "";

  @JsonProperty("mesh_terms")
  @Column(name = "mesh_terms")
  private String meshTerms;

  @JsonProperty("search_name")
  @Column(name = "search_name")
  private String searchName;

  @JsonProperty("timestamp")
  @Column(name = "timestamp")
  private OffsetDateTime timestamp;
}
