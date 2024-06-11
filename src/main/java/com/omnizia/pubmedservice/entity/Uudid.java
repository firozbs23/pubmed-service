package com.omnizia.pubmedservice.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "t_uudid")
public class Uudid {
  @Id
  @Column(name = "uuid")
  @JsonProperty("uuid")
  private String uuid;

  @Column(name = "type")
  @JsonProperty("type")
  private String type;

  @Column(name = "matching_external_id")
  @JsonProperty("matching_external_id")
  private String matchingExternalId;

  @Column(name = "hcp_id")
  @JsonProperty("hcp_id")
  private String hcpId;

  @Column(name = "ranking")
  @JsonProperty("ranking")
  private Integer ranking;
}
