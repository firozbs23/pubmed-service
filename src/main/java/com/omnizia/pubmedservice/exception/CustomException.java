package com.omnizia.pubmedservice.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends RuntimeException {
  private String type;
  private String message;

  public CustomException(String type, String message) {
    super(message);
    this.type = type;
    this.message = message;
  }
}
