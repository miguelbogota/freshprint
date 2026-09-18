package com.freshprint.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

/** An expected API error with a stable code. */
public class ApiException extends RuntimeException {

  private final HttpStatus status;
  private final Map<String, Object> details;

  public ApiException(HttpStatus status, String code) {
    this(status, code, Map.of());
  }

  public ApiException(HttpStatus status, String code, Map<String, Object> details) {
    super(code);
    this.status = status;
    this.details = details;
  }

  public HttpStatus status() {
    return status;
  }

  public Map<String, Object> details() {
    return details;
  }
}
