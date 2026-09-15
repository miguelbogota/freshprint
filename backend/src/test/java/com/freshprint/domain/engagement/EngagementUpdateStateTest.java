package com.freshprint.domain.engagement;

import com.freshprint.domain.template.TemplateMetadata;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies the allowed engagement update states.
 */
class EngagementUpdateStateTest {

  private static final Instant PUBLISHED_AT = Instant.parse("2026-08-18T13:00:00Z");

  /**
   * Confirms valid statuses and rejects impossible template-version combinations.
   */
  @Test
  void representsValidStatesAndRejectsInvalidOnes() {
    var currentEngagement = engagement(5);
    var pendingEngagement = engagement(3);
    var template = template("AUDIT-CA", 5);
    var anotherTemplate = template("REVIEW-CA", 8);

    EngagementUpdateState current =
        new EngagementUpdateState.Current(currentEngagement, template);
    EngagementUpdateState pending =
        new EngagementUpdateState.Pending(pendingEngagement, template, 2);
    EngagementUpdateState unknown =
        new EngagementUpdateState.Unknown("ENG-1003", "METADATA_NOT_INDEXED");

    assertAll(
        () -> assertEquals(UpdateStatus.CURRENT, current.status()),
        () -> assertEquals(UpdateStatus.PENDING, pending.status()),
        () -> assertEquals(UpdateStatus.UNKNOWN, unknown.status()),
        () -> assertThrows(
            IllegalArgumentException.class,
            () -> new EngagementUpdateState.Current(pendingEngagement, template)),
        () -> assertThrows(
            IllegalArgumentException.class,
            () -> new EngagementUpdateState.Pending(currentEngagement, template, 1)),
        () -> assertThrows(
            IllegalArgumentException.class,
            () -> new EngagementUpdateState.Pending(pendingEngagement, anotherTemplate, 2)));
  }

  /**
   * Creates an audit engagement baseline for the given version.
   *
   * @param templateVersion engagement baseline version
   * @return valid engagement baseline
   */
  private EngagementBaseline engagement(int templateVersion) {
    return new EngagementBaseline(
        "ENG-1003",
        "Harbourview Logistics 2026",
        "AUDIT-CA",
        templateVersion);
  }

  /**
   * Creates template metadata for the given ID and latest version.
   *
   * @param templateId template identifier
   * @param latestVersion latest published version
   * @return valid template metadata
   */
  private TemplateMetadata template(String templateId, int latestVersion) {
    return new TemplateMetadata(
        templateId,
        "Canadian Engagement",
        latestVersion,
        PUBLISHED_AT);
  }
}
