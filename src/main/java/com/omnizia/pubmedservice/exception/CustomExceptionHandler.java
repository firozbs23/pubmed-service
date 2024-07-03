package com.omnizia.pubmedservice.exception;

import com.omnizia.pubmedservice.model.ClientErrorBody;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ClientErrorBody> handleCustomException(CustomException exception) {
    HttpServletRequest request =
        ((ServletRequestAttributes)
                Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
            .getRequest();
    // Get the endpoint URL
    String endpoint = request.getRequestURI();
    ClientErrorBody errorBody =
        ClientErrorBody.builder()
            .type(exception.getType())
            .title(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .status(HttpStatus.BAD_REQUEST.value())
            .detail(exception.getMessage())
            .instance(endpoint)
            .build();
    return ResponseEntity.badRequest().body(errorBody);
  }
}
