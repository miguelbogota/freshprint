package com.freshprint.repository;

import com.freshprint.dto.OperationResponse;
import java.time.Instant;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Stores decision receipts and their final outcomes. */
@Repository
public class OperationRepository {

  private final JdbcTemplate jdbc;

  public OperationRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public boolean hasActive(String engagementId) {
    var count = jdbc.queryForObject("SELECT COUNT(*) FROM operations WHERE engagement_id = ? "
        + "AND status IN ('ACCEPTED', 'RUNNING')", Integer.class, engagementId);
    return count != null && count > 0;
  }

  public void create(String id, String engagementId, String decision, int baseline, int target) {
    jdbc.update("INSERT INTO operations (operation_id, engagement_id, decision, baseline_version, "
        + "target_version, status, created_at) VALUES (?, ?, ?, ?, ?, 'ACCEPTED', ?)",
        id, engagementId, decision, baseline, target, Instant.now());
  }

  public void running(String id) {
    jdbc.update("UPDATE operations SET status = 'RUNNING' WHERE operation_id = ?", id);
  }

  public void completed(String id, String status, String message) {
    jdbc.update("UPDATE operations SET status = ?, message = ?, completed_at = ? "
        + "WHERE operation_id = ?", status, message, Instant.now(), id);
  }

  public Optional<OperationResponse> find(String id) {
    return jdbc.query("SELECT operation_id, engagement_id, decision, status, message, "
        + "created_at, completed_at FROM operations WHERE operation_id = ?", (rs, row) -> {
          var completed = rs.getTimestamp("completed_at");
          return new OperationResponse(rs.getString("operation_id"),
              rs.getString("engagement_id"), rs.getString("decision"),
              rs.getString("status"), rs.getString("message"),
              rs.getTimestamp("created_at").toInstant().toString(),
              completed == null ? null : completed.toInstant().toString());
        }, id).stream().findFirst();
  }
}
