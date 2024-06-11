package com.omnizia.pubmedservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TestDto {

  private String title;
  private String author;
  private String journal;
}
