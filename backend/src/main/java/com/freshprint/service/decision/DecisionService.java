package com.freshprint.service.decision;

import com.freshprint.dto.DecisionReceipt;
import com.freshprint.dto.DecisionRequest;
import com.freshprint.dto.OperationResponse;
import com.freshprint.exception.ApiException;
import com.freshprint.model.engagement.UpdateStatus;
import com.freshprint.repository.EngagementRepository;
import com.freshprint.repository.FixtureCatalog;
import com.freshprint.repository.OperationRepository;
import com.freshprint.service.update.EngagementUpdateEvaluator;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/** Validates the reviewed versions and starts an asynchronous decision. */
@Service
public class DecisionService {

  private final EngagementRepository engagements;
  private final EngagementUpdateEvaluator evaluator;
  private final OperationRepository operations;
  private final DecisionWorker worker;

  public DecisionService(EngagementRepository engagements, FixtureCatalog templates,
      OperationRepository operations, DecisionWorker worker) {
    this.engagements = engagements;
    this.evaluator = new EngagementUpdateEvaluator(templates);
    this.operations = operations;
    this.worker = worker;
  }

  public synchronized DecisionReceipt decide(String id, DecisionRequest request) {
    if (request == null || !("APPLY".equals(request.decision())
        || "DECLINE".equals(request.decision()))) {
      throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_DECISION");
    }
    var engagement = engagements.find(id)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ENGAGEMENT_NOT_FOUND"));
    var update = evaluator.evaluate(engagement);
    if (update.status() != UpdateStatus.PENDING
        || engagement.templateVersion() != request.expectedBaselineVersion()
        || update.targetVersion() != request.targetVersion()) {
      throw new ApiException(HttpStatus.CONFLICT, "VERSION_CONFLICT", Map.of(
          "currentBaselineVersion", engagement.templateVersion(),
          "currentTargetVersion", update.targetVersion()));
    }
    if (Integer.valueOf(update.targetVersion()).equals(engagements.declinedTarget(id))) {
      throw new ApiException(HttpStatus.CONFLICT, "UPDATE_ALREADY_DECLINED");
    }
    if (operations.hasActive(id)) {
      throw new ApiException(HttpStatus.CONFLICT, "DECISION_IN_PROGRESS");
    }
    var operationId = "OP-" + UUID.randomUUID();
    operations.create(operationId, id, request.decision(), request.expectedBaselineVersion(),
        request.targetVersion());
    worker.process(operationId, id, request.decision(), request.expectedBaselineVersion(),
        request.targetVersion());
    return new DecisionReceipt(operationId, "ACCEPTED");
  }

  public OperationResponse operation(String id) {
    return operations.find(id)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "OPERATION_NOT_FOUND"));
  }
}
