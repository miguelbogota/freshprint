package com.freshprint.model.summary;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * A human-readable summary generated from a raw template diff.
 *
 * @param generatedAt time when the summary was generated
 * @param groups      changes grouped by user-facing template section
 */
public record ChangeSummary(Instant generatedAt, List<SummaryGroup> groups) {

  /**
   * Validates the generation time and makes the groups list immutable.
   */
  public ChangeSummary {
    Objects.requireNonNull(generatedAt, "generatedAt must not be null");
    groups = List.copyOf(groups);
  }
}
