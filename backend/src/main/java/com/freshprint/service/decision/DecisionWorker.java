package com.freshprint.service.decision;

import com.freshprint.repository.EngagementRepository;
import com.freshprint.repository.OperationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/** Completes an accepted decision without making the HTTP request wait. */
@Service
public class DecisionWorker {

  private final EngagementRepository engagements;
  private final OperationRepository operations;

  public DecisionWorker(EngagementRepository engagements, OperationRepository operations) {
    this.engagements = engagements;
    this.operations = operations;
  }

  @Async
  public void process(String operationId, String engagementId, String decision,
      int baseline, int target) {
    operations.running(operationId);
    try {
      var updated = "APPLY".equals(decision)
          ? engagements.apply(engagementId, baseline, target)
          : engagements.decline(engagementId, baseline, target);
      if (!updated)
        throw new IllegalStateException("The engagement changed during processing");
      operations.completed(operationId, "SUCCEEDED", null);
    } catch (Exception exception) {
      operations.completed(operationId, "FAILED", exception.getMessage());
    }
  }
}
