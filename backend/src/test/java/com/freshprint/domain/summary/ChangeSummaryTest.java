package com.freshprint.domain.summary;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Verifies the human-readable summary model.
 */
class ChangeSummaryTest {

  /**
   * Confirms summary groups keep an immutable copy of their changes.
   */
  @Test
  void protectsSummarizedChangesFromExternalModification() {
    var changes = new ArrayList<SummaryChange>();
    changes.add(new SummaryChange(
        SummaryChangeKind.CHANGED,
        "Materiality threshold changed from 5% to 4%.",
        false));

    var group = new SummaryGroup("Materiality", changes);
    var summary = new ChangeSummary(
        Instant.parse("2026-08-18T13:00:00Z"),
        List.of(group));
    changes.clear();

    assertEquals(1, summary.groups().getFirst().changes().size());
  }
}
