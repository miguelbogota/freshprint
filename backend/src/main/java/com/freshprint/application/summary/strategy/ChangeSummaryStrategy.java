package com.freshprint.application.summary.strategy;

import com.freshprint.domain.summary.SummaryChange;
import com.freshprint.domain.template.TemplateChange;

import java.util.Objects;

/**
 * Converts one supported raw template change into a readable result.
 */
public interface ChangeSummaryStrategy {

  /**
   * Checks whether this strategy understands a raw change.
   *
   * @param change raw template change
   * @return {@code true} when this strategy can summarize the change
   */
  boolean supports(TemplateChange change);

  /**
   * Converts a supported raw change into a section and readable change.
   *
   * @param change raw template change
   * @return summarized change and its user-facing section
   */
  Result summarize(TemplateChange change);

  /**
   * A summarized change paired with its user-facing section.
   *
   * @param section section used to group the change
   * @param change  readable change
   */
  record Result(String section, SummaryChange change) {

    /**
     * Validates the section and summarized change.
     */
    public Result {
      Objects.requireNonNull(section, "section must not be null");
      Objects.requireNonNull(change, "change must not be null");
      if (section.isBlank()) {
        throw new IllegalArgumentException("section must not be blank");
      }
    }
  }
}
