package com.freshprint.application.summary.strategy;

import java.util.List;

/**
 * Provides the standard ordered strategies used by Freshprint.
 */
public final class StandardSummaryStrategies {

  /**
   * Prevents construction of this utility class.
   */
  private StandardSummaryStrategies() {
  }

  /**
   * Creates the standard strategy order with the fallback last.
   *
   * @return immutable ordered strategies
   */
  public static List<ChangeSummaryStrategy> create() {
    return List.of(
        new QuestionChangeSummaryStrategy(),
        new SectionChangeSummaryStrategy(),
        new FallbackChangeSummaryStrategy());
  }
}
