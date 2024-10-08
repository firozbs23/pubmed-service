package com.omnizia.pubmedservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PubmedDataDto {
  @JsonProperty("transaction_viq_id")
  @Builder.Default
  private String transactionViqId = "";

  @JsonProperty("hcp_viq_id")
  @Builder.Default
  private String hcpViqId = "";

  @JsonProperty("country_iso2")
  @Builder.Default
  private String countryIso2 = "";

  @JsonProperty("job_id")
  @Builder.Default
  private String jobId = "";

  @JsonProperty("specialty_code")
  @Builder.Default
  private String specialtyCode = "";

  @JsonProperty("publication_id")
  @Builder.Default
  private String publicationId = "";

  @JsonProperty("title")
  @Builder.Default
  private String title = "";

  @JsonProperty("job_title")
  @Builder.Default
  private String jobTitle = "";

  @JsonProperty("journal")
  @Builder.Default
  private String journal = "";

  @JsonProperty("publication_date")
  @Builder.Default
  private String publicationDate = "";

  @JsonProperty("abstract")
  @Builder.Default
  private String abstractValue = "";

  @JsonProperty("hcp_role")
  @Builder.Default
  private String hcpRole = "";

  @JsonProperty("publication_type")
  @Builder.Default
  private String publicationType = "";

  @JsonProperty("issn")
  @Builder.Default
  private String issn = "";

  @Builder.Default private String url = "";

  @JsonProperty("gds_tag_viq_id")
  @Builder.Default
  private String gdsTagViqId = "";

  @JsonProperty("hcp_role_viq_id")
  @Builder.Default
  private String hcpRoleViqId = "";

  @JsonProperty("key")
  @Builder.Default
  private String key = "";

  @JsonProperty("created_by_job")
  @Builder.Default
  private String createdByJob = "";

  @JsonProperty("updated_by_job")
  @Builder.Default
  private String updatedByJob = "";

  @JsonProperty("created_at")
  @Builder.Default
  private String createdAt = "";

  @JsonProperty("updated_at")
  @Builder.Default
  private String updatedAt = "";

  @JsonProperty("first_name")
  @Builder.Default
  private String firstName = "";

  @JsonProperty("last_name")
  @Builder.Default
  private String lastName = "";

  @JsonProperty("initials")
  @Builder.Default
  private String initials = "";

  @JsonProperty("full_name")
  @Builder.Default
  private String fullName = "";

  @JsonProperty("affiliations")
  @Builder.Default
  private String affiliations = "";

  @JsonProperty("pmcid")
  @Builder.Default
  private String pmcid = "";

  @JsonProperty("doi")
  @Builder.Default
  private String doi = "";

  @JsonProperty("mesh_terms")
  @Builder.Default
  private String meshTerms = "";

  @JsonProperty("publication_platform")
  @Builder.Default
  private String publicationPlatform = "";

  @JsonProperty("search_name")
  @Builder.Default
  private String searchName = "";

  @JsonProperty("tag_type")
  @Builder.Default
  private String tagType = "";

  @JsonProperty("tag_category")
  @Builder.Default
  private String tagCategory = "";

  @JsonProperty("tag_value")
  @Builder.Default
  private String tagValue = "";

  @JsonProperty("chat_summary")
  @Builder.Default
  private String chatSummary = "";

  @JsonProperty("chat_positive")
  @Builder.Default
  private String chatPositive = "";

  @JsonProperty("chat_negative")
  @Builder.Default
  private String chatNegative = "";

  @JsonProperty("timestamp")
  @Builder.Default
  private String timestamp = "";
}
