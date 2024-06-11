package com.omnizia.pubmedservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "t_publications")
public class Publications {

  @Id
  @Column(name = "transaction_viq_id")
  @JsonProperty("transaction_viq_id")
  private String transactionViqId;

  @Column(name = "hcp_viq_id")
  @JsonProperty("hcp_viq_id")
  private String hcpViqId;

  @Column(name = "country_iso2")
  @JsonProperty("country_iso2")
  private String countryIso2;

  @Column(name = "specialty_code")
  @JsonProperty("specialty_code")
  private String specialtyCode;

  @Column(name = "publication_id")
  @JsonProperty("publication_id")
  private String publicationId;

  @Column(name = "title")
  @JsonProperty("title")
  private String title;

  @Column(name = "journal")
  @JsonProperty("journal")
  private String journal;

  @Column(name = "publication_date")
  @JsonProperty("publication_date")
  private String publicationDate;

  @Column(name = "abstract")
  @JsonProperty("abstract")
  private String abstractValue;

  @Column(name = "hcp_role")
  @JsonProperty("hcp_role")
  private String hcpRole;

  @Column(name = "publication_type")
  @JsonProperty("publication_type")
  private String publicationType;

  @Column(name = "issn")
  @JsonProperty("issn")
  private String issn;

  @Column(name = "url")
  @JsonProperty("url")
  private String url;

  @Column(name = "gds_tag_viq_id")
  @JsonProperty("gds_tag_viq_id")
  private String gdsTagViqId;

  @Column(name = "hcp_role_viq_id")
  @JsonProperty("hcp_role_viq_id")
  private String hcpRoleViqId;

  @Column(name = "key")
  @JsonProperty("key")
  private Long key;

  @Column(name = "created_by_job")
  @JsonProperty("created_by_job")
  private String createdByJob;

  @Column(name = "updated_by_job")
  @JsonProperty("updated_by_job")
  private String updatedByJob;

  @Column(name = "created_at")
  @JsonProperty("created_at")
  private String createdAt;

  @Column(name = "updated_at")
  @JsonProperty("updated_at")
  private String updatedAt;
}
