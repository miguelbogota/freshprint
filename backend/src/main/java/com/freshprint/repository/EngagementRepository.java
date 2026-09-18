package com.freshprint.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshprint.model.engagement.EngagementBaseline;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** A durable metadata index; list requests never open a full engagement. */
@Repository
public class EngagementRepository {

  private final JdbcTemplate jdbc;

  public EngagementRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public List<EngagementBaseline> all() {
    return jdbc.query("SELECT engagement_id, name, template_id, template_version FROM engagements ORDER BY name",
        (rs, row) -> new EngagementBaseline(rs.getString(1), rs.getString(2),
            rs.getString(3), rs.getInt(4)));
  }

  public Optional<EngagementBaseline> find(String id) {
    return jdbc
        .query("SELECT engagement_id, name, template_id, template_version FROM engagements WHERE engagement_id = ?",
            (rs, row) -> new EngagementBaseline(rs.getString(1), rs.getString(2),
                rs.getString(3), rs.getInt(4)),
            id)
        .stream().findFirst();
  }

  public Integer declinedTarget(String id) {
    return jdbc.queryForObject("SELECT declined_target FROM engagements WHERE engagement_id = ?",
        Integer.class, id);
  }

  public boolean apply(String id, int baseline, int target) {
    return jdbc.update("UPDATE engagements SET template_version = ?, declined_target = NULL "
        + "WHERE engagement_id = ? AND template_version = ?", target, id, baseline) == 1;
  }

  public boolean decline(String id, int baseline, int target) {
    return jdbc.update("UPDATE engagements SET declined_target = ? "
        + "WHERE engagement_id = ? AND template_version = ?", target, id, baseline) == 1;
  }

  @Bean
  ApplicationRunner seedEngagements(JdbcTemplate jdbc, ObjectMapper mapper) {
    return args -> {
      jdbc.update("UPDATE operations SET status = 'FAILED', message = 'Server restarted before completion' "
          + "WHERE status IN ('ACCEPTED', 'RUNNING')");
      if (jdbc.queryForObject("SELECT COUNT(*) FROM engagements", Integer.class) != 0)
        return;
      try (var input = new ClassPathResource("fixtures/engagements.json").getInputStream()) {
        for (var node : mapper.readTree(input)) {
          jdbc.update(
              "INSERT INTO engagements (engagement_id, name, template_id, template_version) VALUES (?, ?, ?, ?)",
              node.path("engagementId").asText(), node.path("name").asText(),
              node.path("templateId").asText(), node.path("templateVersion").asInt());
        }
      } catch (IOException exception) {
        throw new IllegalStateException("Could not seed demo engagements", exception);
      }
    };
  }
}
