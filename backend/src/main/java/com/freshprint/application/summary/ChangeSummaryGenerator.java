package com.freshprint.application.summary;

import com.freshprint.application.summary.strategy.ChangeSummaryStrategy;
import com.freshprint.domain.summary.ChangeSummary;
import com.freshprint.domain.summary.SummaryChange;
import com.freshprint.domain.summary.SummaryGroup;
import com.freshprint.domain.template.TemplateDiff;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Turns a raw template diff into changes that a user can review.
 */
public final class ChangeSummaryGenerator {

  private final List<ChangeSummaryStrategy> strategies;
  private final Clock clock;

  /**
   * Creates a generator with ordered strategies and a clock.
   *
   * <br>
   * The first strategy that supports a change is used.
   *
   * @param strategies ordered summary strategies, including a fallback
   * @param clock      clock used to record when a summary was generated
   */
  public ChangeSummaryGenerator(List<ChangeSummaryStrategy> strategies, Clock clock) {
    this.strategies = List.copyOf(strategies);
    this.clock = Objects.requireNonNull(clock, "clock must not be null");

    if (this.strategies.isEmpty()) {
      throw new IllegalArgumentException("strategies must not be empty");
    }
  }

  /**
   * Summarizes every raw change and groups the results by template section.
   *
   * @param diff raw effective diff from the baseline to the latest version
   * @return generated human-readable summary
   */
  public ChangeSummary generate(TemplateDiff diff) {
    Objects.requireNonNull(diff, "diff must not be null");

    Map<String, List<SummaryChange>> changesBySection = new LinkedHashMap<>();
    for (var change : diff.changes()) {
      var result = strategies.stream()
          .filter(strategy -> strategy.supports(change))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException(
              "No summary strategy supports path " + change.path()))
          .summarize(change);

      changesBySection
          .computeIfAbsent(result.section(), ignored -> new ArrayList<>())
          .add(result.change());
    }

    var groups = changesBySection.entrySet().stream()
        .map(entry -> new SummaryGroup(entry.getKey(), entry.getValue()))
        .toList();

    return new ChangeSummary(Instant.now(clock), groups);
  }
}
