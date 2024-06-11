package com.omnizia.pubmedservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicationsDto {

  @JsonProperty("transaction_viq_id")
  private String transactionViqId;

  @JsonProperty("hcp_viq_id")
  private String hcpViqId;

  @JsonProperty("country_iso2")
  private String countryIso2;

  @JsonProperty("specialty_code")
  private String specialtyCode;

  @JsonProperty("publication_id")
  private String publicationId;

  @JsonProperty("title")
  private String title;

  @JsonProperty("journal")
  private String journal;

  @JsonProperty("publication_date")
  private String publicationDate;

  @JsonProperty("abstract")
  private String abstractValue;

  @JsonProperty("hcp_role")
  private String hcpRole;

  @JsonProperty("publication_type")
  private String publicationType;

  @JsonProperty("issn")
  private String issn;

  @JsonProperty("url")
  private String url;

  @JsonProperty("gds_tag_viq_id")
  private String gdsTagViqId;

  @JsonProperty("hcp_role_viq_id")
  private String hcpRoleViqId;

  @JsonProperty("key")
  private Long key;

  @JsonProperty("created_by_job")
  private String createdByJob;

  @JsonProperty("updated_by_job")
  private String updatedByJob;

  @JsonProperty("created_at")
  private String createdAt;

  @JsonProperty("updated_at")
  private String updatedAt;
}
