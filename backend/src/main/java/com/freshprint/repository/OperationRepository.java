package com.freshprint.repository;

import com.freshprint.dto.OperationResponse;
import com.freshprint.model.engagement.OperationEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Stores accepted decisions and maps their eventual outcomes to API values. */
@Repository
public class OperationRepository {

  private final OperationJpaRepository jpa;

  public OperationRepository(OperationJpaRepository jpa) {
    this.jpa = jpa;
  }

  public boolean hasActive(String engagementId) {
    return jpa.existsByEngagementIdAndStatusIn(engagementId, List.of("ACCEPTED", "RUNNING"));
  }

  public void create(String id, String engagementId, String decision, int baseline, int target) {
    jpa.save(new OperationEntity(id, engagementId, decision, baseline, target));
  }

  @Transactional
  public void running(String id) {
    var operation = jpa.findById(id).orElseThrow();
    operation.running();
  }

  @Transactional
  public void completed(String id, String status, String message) {
    var operation = jpa.findById(id).orElseThrow();
    operation.complete(status, message);
  }

  @Transactional
  public void failInterrupted() {
    for (var operation : jpa.findAllByStatusIn(List.of("ACCEPTED", "RUNNING"))) {
      operation.complete("FAILED", "Server restarted before completion");
    }
  }

  public Optional<OperationResponse> find(String id) {
    return jpa.findById(id).map(operation -> new OperationResponse(
        operation.getOperationId(), operation.getEngagementId(), operation.getDecision(),
        operation.getStatus(), operation.getMessage(), operation.getCreatedAt().toString(),
        operation.getCompletedAt() == null ? null : operation.getCompletedAt().toString()));
  }
}
