package com.freshprint.api;

import com.freshprint.application.update.EngagementUpdateEvaluator;
import com.freshprint.domain.engagement.UpdateStatus;
import com.freshprint.infrastructure.EngagementRepository;
import com.freshprint.infrastructure.FixtureCatalog;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Accepts versioned decisions and exposes the eventual operation outcome. */
@RestController
@RequestMapping("/api")
public class DecisionApi {

  private final EngagementRepository engagements;
  private final EngagementUpdateEvaluator evaluator;
  private final JdbcTemplate jdbc;
  private final DecisionWorker worker;

  public DecisionApi(EngagementRepository engagements, FixtureCatalog templates,
      JdbcTemplate jdbc, DecisionWorker worker) {
    this.engagements = engagements;
    this.evaluator = new EngagementUpdateEvaluator(templates);
    this.jdbc = jdbc;
    this.worker = worker;
  }

  public record DecisionRequest(String decision, int expectedBaselineVersion, int targetVersion) {}

  @PostMapping("/engagements/{id}/template-update-decisions")
  public synchronized Map<String, String> decide(@PathVariable("id") String id,
      @RequestBody DecisionRequest request) {
    if (request == null || !("APPLY".equals(request.decision())
        || "DECLINE".equals(request.decision()))) {
      throw new ApiError(HttpStatus.BAD_REQUEST, "INVALID_DECISION");
    }
    var engagement = engagements.find(id)
        .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "ENGAGEMENT_NOT_FOUND"));
    var update = evaluator.evaluate(engagement);
    if (update.status() != UpdateStatus.PENDING
        || engagement.templateVersion() != request.expectedBaselineVersion()
        || update.targetVersion() != request.targetVersion()) {
      throw new ApiError(HttpStatus.CONFLICT, "VERSION_CONFLICT", Map.of(
          "currentBaselineVersion", engagement.templateVersion(),
          "currentTargetVersion", update.targetVersion()));
    }
    if (Integer.valueOf(update.targetVersion()).equals(engagements.declinedTarget(id))) {
      throw new ApiError(HttpStatus.CONFLICT, "UPDATE_ALREADY_DECLINED");
    }
    var active = jdbc.queryForObject("SELECT COUNT(*) FROM operations WHERE engagement_id = ? "
        + "AND status IN ('ACCEPTED', 'RUNNING')", Integer.class, id);
    if (active != null && active > 0) {
      throw new ApiError(HttpStatus.CONFLICT, "DECISION_IN_PROGRESS");
    }
    var operationId = "OP-" + UUID.randomUUID();
    jdbc.update("INSERT INTO operations (operation_id, engagement_id, decision, baseline_version, "
        + "target_version, status, created_at) VALUES (?, ?, ?, ?, ?, 'ACCEPTED', ?)",
        operationId, id, request.decision(), request.expectedBaselineVersion(),
        request.targetVersion(), Instant.now());
    worker.process(operationId, id, request.decision(), request.expectedBaselineVersion(),
        request.targetVersion());
    return Map.of("operationId", operationId, "status", "ACCEPTED");
  }

  @GetMapping("/template-update-operations/{id}")
  public Map<String, Object> operation(@PathVariable("id") String id) {
    return jdbc.query("SELECT operation_id, engagement_id, decision, status, message, "
        + "created_at, completed_at FROM operations WHERE operation_id = ?", (rs, row) -> {
          var result = new java.util.LinkedHashMap<String, Object>();
          result.put("operationId", rs.getString("operation_id"));
          result.put("engagementId", rs.getString("engagement_id"));
          result.put("decision", rs.getString("decision"));
          result.put("status", rs.getString("status"));
          if (rs.getString("message") != null) result.put("message", rs.getString("message"));
          result.put("createdAt", rs.getTimestamp("created_at").toInstant().toString());
          if (rs.getTimestamp("completed_at") != null) {
            result.put("completedAt", rs.getTimestamp("completed_at").toInstant().toString());
          }
          return result;
        }, id).stream().findFirst()
        .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "OPERATION_NOT_FOUND"));
  }
}
