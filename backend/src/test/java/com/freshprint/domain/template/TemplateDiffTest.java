package com.freshprint.domain.template;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies raw template-diff behavior.
 */
class TemplateDiffTest {

  /**
   * Confirms operation mapping and protects a diff from external list changes.
   */
  @Test
  void reportsOperationsAndProtectsItsChanges() {
    var changes = new ArrayList<TemplateChange>();
    changes.add(new TemplateChange.Replaced(
        "/sections/materiality/guidance/thresholdPercent",
        5.0,
        4.0));

    var diff = new TemplateDiff(
        "AUDIT-CA",
        3,
        5,
        Instant.parse("2026-08-18T13:00:00Z"),
        changes);
    changes.add(new TemplateChange.Added("/sections/planning/questions/7", "question"));

    assertAll(
        () -> assertEquals(1, diff.changes().size()),
        () -> assertEquals(ChangeOperation.REPLACE, diff.changes().getFirst().operation()),
        () -> assertThrows(
            UnsupportedOperationException.class,
            () -> diff.changes().add(new TemplateChange.Removed("/metadata/name", "old"))));
  }
}
