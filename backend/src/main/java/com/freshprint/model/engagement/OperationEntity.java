package com.freshprint.model.engagement;

import java.time.Instant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Persistent state for an asynchronous Apply or Decline decision. */
@Entity
@Table(name = "operations")
public class OperationEntity {

  @Id
  @Column(name = "operation_id", length = 80)
  private String operationId;

  @Column(name = "engagement_id", nullable = false, length = 80)
  private String engagementId;

  @Column(name = "decision", nullable = false, length = 12)
  private String decision;

  @Column(name = "baseline_version", nullable = false)
  private int baselineVersion;

  @Column(name = "target_version", nullable = false)
  private int targetVersion;

  @Column(name = "status", nullable = false, length = 16)
  private String status;

  @Column(name = "message", length = 255)
  private String message;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "completed_at")
  private Instant completedAt;

  protected OperationEntity() {
  }

  public OperationEntity(String operationId, String engagementId, String decision,
      int baselineVersion, int targetVersion) {
    this.operationId = operationId;
    this.engagementId = engagementId;
    this.decision = decision;
    this.baselineVersion = baselineVersion;
    this.targetVersion = targetVersion;
    this.status = "ACCEPTED";
    this.createdAt = Instant.now();
  }

  public String getOperationId() {
    return operationId;
  }

  public String getEngagementId() {
    return engagementId;
  }

  public String getDecision() {
    return decision;
  }

  public String getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getCompletedAt() {
    return completedAt;
  }

  public void running() {
    status = "RUNNING";
  }

  public void complete(String outcome, String details) {
    status = outcome;
    message = details;
    completedAt = Instant.now();
  }
}
