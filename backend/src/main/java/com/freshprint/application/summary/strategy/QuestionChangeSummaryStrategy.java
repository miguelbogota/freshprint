package com.freshprint.application.summary.strategy;

import com.freshprint.domain.summary.SummaryChange;
import com.freshprint.domain.summary.SummaryChangeKind;
import com.freshprint.domain.template.TemplateChange;

/**
 * Creates friendly descriptions for question changes.
 */
public final class QuestionChangeSummaryStrategy implements ChangeSummaryStrategy {

  /**
   * Creates a question change summary strategy.
   */
  public QuestionChangeSummaryStrategy() {
  }

  /**
   * Supports paths that point to a template question.
   *
   * @param change raw template change
   * @return whether the path contains a questions collection
   */
  @Override
  public boolean supports(TemplateChange change) {
    return change.path().contains("/questions/");
  }

  /**
   * Describes a question addition, replacement, or removal.
   *
   * @param change raw question change
   * @return readable question change
   */
  @Override
  public Result summarize(TemplateChange change) {
    var section = SummaryStrategySupport.sectionName(change.path());
    var description = switch (change) {
      case TemplateChange.Added added ->
        "Added question " + SummaryStrategySupport.displayValue(added.value()) + ".";
      case TemplateChange.Replaced replaced ->
        "Question content changed from "
            + SummaryStrategySupport.displayValue(replaced.oldValue())
            + " to "
            + SummaryStrategySupport.displayValue(replaced.newValue())
            + ".";
      case TemplateChange.Removed removed ->
        "Removed question content "
            + SummaryStrategySupport.displayValue(removed.oldValue())
            + ".";
    };

    return new Result(
        section,
        new SummaryChange(kind(change), description, false));
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
