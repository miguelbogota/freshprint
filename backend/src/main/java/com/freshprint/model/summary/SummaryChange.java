package com.freshprint.model.summary;

import java.util.Objects;

/**
 * One human-readable template change.
 *
 * @param kind              user-facing category of change
 * @param description       plain-language explanation
 * @param reviewRecommended whether the fallback summary needs closer review
 */
public record SummaryChange(
    SummaryChangeKind kind,
    String description,
    boolean reviewRecommended) {

  /**
   * Validates that the summary has a kind and readable description.
   */
  public SummaryChange {
    Objects.requireNonNull(kind, "kind must not be null");
    Objects.requireNonNull(description, "description must not be null");

    if (description.isBlank()) {
      throw new IllegalArgumentException("description must not be blank");
    }
  }
}
