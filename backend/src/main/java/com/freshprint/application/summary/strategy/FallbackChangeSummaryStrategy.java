package com.freshprint.application.summary.strategy;

import com.freshprint.domain.summary.SummaryChange;
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
        new SummaryChange(SummaryStrategySupport.kind(change), description, true));
  }
}
