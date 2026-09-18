package com.freshprint.api;

import com.freshprint.infrastructure.EngagementRepository;
import java.time.Instant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/** Completes an accepted decision without making the HTTP request wait. */
@Service
public class DecisionWorker {

  private final EngagementRepository engagements;
  private final JdbcTemplate jdbc;

  public DecisionWorker(EngagementRepository engagements, JdbcTemplate jdbc) {
    this.engagements = engagements;
    this.jdbc = jdbc;
  }

  @Async
  public void process(String operationId, String engagementId, String decision,
      int baseline, int target) {
    jdbc.update("UPDATE operations SET status = 'RUNNING' WHERE operation_id = ?", operationId);
    try {
      var updated = "APPLY".equals(decision)
          ? engagements.apply(engagementId, baseline, target)
          : engagements.decline(engagementId, baseline, target);
      if (!updated) throw new IllegalStateException("The engagement changed during processing");
      jdbc.update("UPDATE operations SET status = 'SUCCEEDED', completed_at = ? "
          + "WHERE operation_id = ?", Instant.now(), operationId);
    } catch (Exception exception) {
      jdbc.update("UPDATE operations SET status = 'FAILED', message = ?, completed_at = ? "
          + "WHERE operation_id = ?", exception.getMessage(), Instant.now(), operationId);
    }
  }
}
