package com.freshprint.service.decision;

import com.freshprint.repository.OperationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/** Completes an accepted decision without making the HTTP request wait. */
@Service
public class DecisionWorker {

  private final DecisionCompletionService completion;
  private final OperationRepository operations;

  public DecisionWorker(DecisionCompletionService completion, OperationRepository operations) {
    this.completion = completion;
    this.operations = operations;
  }

  @Async
  public void process(String operationId, String engagementId, String decision,
      int baseline, int target) {
    operations.running(operationId);
    try {
      completion.complete(operationId, engagementId, decision, baseline, target);
    } catch (Exception exception) {
      operations.completed(operationId, "FAILED", exception.getMessage());
    }
  }
}
