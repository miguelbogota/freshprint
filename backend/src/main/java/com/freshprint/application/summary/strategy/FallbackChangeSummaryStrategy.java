package com.freshprint.application.summary.strategy;

import com.freshprint.domain.summary.SummaryChange;
import com.freshprint.domain.summary.SummaryChangeKind;
import com.freshprint.domain.template.TemplateChange;

/**
 * Keeps unrecognized raw changes visible for manual review.
 */
public final class FallbackChangeSummaryStrategy implements ChangeSummaryStrategy {

  /**
   * Creates a fallback change summary strategy.
   */
  public FallbackChangeSummaryStrategy() {
  }

  /**
   * Supports every change not handled by an earlier strategy.
   *
   * @param change raw template change
   * @return always {@code true}
   */
  @Override
  public boolean supports(TemplateChange change) {
    return true;
  }

  /**
   * Produces a safe generic description that includes the original path.
   *
   * @param change unrecognized raw change
   * @return fallback result marked for review
   */
  @Override
  public Result summarize(TemplateChange change) {
    var description = "Template content changed at " + change.path() + ".";
    return new Result(
        "Other changes",
        new SummaryChange(kind(change), description, true));
  }

  /**
   * Maps a raw change type to its user-facing category.
   *
   * @param change raw template change
   * @return matching summary kind
   */
  private SummaryChangeKind kind(TemplateChange change) {
    return switch (change) {
      case TemplateChange.Added _ -> SummaryChangeKind.ADDED;
      case TemplateChange.Replaced _ -> SummaryChangeKind.CHANGED;
      case TemplateChange.Removed _ -> SummaryChangeKind.REMOVED;
    };
  }
}
