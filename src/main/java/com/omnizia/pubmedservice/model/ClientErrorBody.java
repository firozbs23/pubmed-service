package com.omnizia.pubmedservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ClientErrorBody {
  private String type;
  private String title;
  private Integer status;
  private String detail;
  private String instance;
}
