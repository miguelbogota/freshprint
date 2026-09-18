package com.freshprint.dto;

/** The eventual outcome of an accepted Apply or Decline request. */
public record OperationResponse(
    String operationId,
    String engagementId,
    String decision,
    String status,
    String message,
    String createdAt,
    String completedAt) {
}
