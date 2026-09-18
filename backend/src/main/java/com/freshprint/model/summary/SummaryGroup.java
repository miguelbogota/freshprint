package com.freshprint.model.summary;

import java.util.List;
import java.util.Objects;

/**
 * Human-readable changes grouped by template section.
 *
 * @param section user-facing section name
 * @param changes non-empty changes in the section
 */
public record SummaryGroup(String section, List<SummaryChange> changes) {

  /**
   * Validates the section and makes the changes list immutable.
   */
  public SummaryGroup {
    Objects.requireNonNull(section, "section must not be null");
    changes = List.copyOf(changes);

    if (section.isBlank()) {
      throw new IllegalArgumentException("section must not be blank");
    }
    if (changes.isEmpty()) {
      throw new IllegalArgumentException("changes must not be empty");
    }
  }
}
