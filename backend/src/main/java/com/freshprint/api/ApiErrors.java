package com.freshprint.api;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Keeps expected failures as small, machine-readable JSON responses. */
@RestControllerAdvice
public class ApiErrors {

  @ExceptionHandler(ApiError.class)
  ResponseEntity<Map<String, Object>> expected(ApiError error) {
    var response = new java.util.LinkedHashMap<String, Object>();
    response.put("code", error.getMessage());
    response.putAll(error.details());
    return ResponseEntity.status(error.status()).body(response);
  }
}
