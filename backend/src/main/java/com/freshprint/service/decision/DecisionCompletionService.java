package com.freshprint.service.decision;

import com.freshprint.repository.EngagementRepository;
import com.freshprint.repository.OperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Commits the engagement change and successful operation result together. */
@Service
public class DecisionCompletionService {

  private final EngagementRepository engagements;
  private final OperationRepository operations;

  public DecisionCompletionService(EngagementRepository engagements,
      OperationRepository operations) {
    this.engagements = engagements;
    this.operations = operations;
  }

  @Transactional
  public void complete(String operationId, String engagementId, String decision,
      int baseline, int target) {
    var updated = "APPLY".equals(decision)
        ? engagements.apply(engagementId, baseline, target)
        : engagements.decline(engagementId, baseline, target);
    if (!updated) {
      throw new IllegalStateException("The engagement changed during processing");
    }
    operations.completed(operationId, "SUCCEEDED", null);
  }
}
