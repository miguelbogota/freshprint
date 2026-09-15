package com.freshprint.application.summary.strategy;

import com.freshprint.domain.summary.SummaryChange;
import com.freshprint.domain.template.TemplateChange;

/**
 * Creates general descriptions for recognized template sections.
 */
public final class SectionChangeSummaryStrategy implements ChangeSummaryStrategy {

  /**
   * Creates a general section change summary strategy.
   */
  public SectionChangeSummaryStrategy() {
  }

  /**
   * Supports non-question paths inside the sections object.
   *
   * @param change raw template change
   * @return whether the path belongs to a template section
   */
  @Override
  public boolean supports(TemplateChange change) {
    return change.path().startsWith("/sections/");
  }

  /**
   * Describes a general addition, replacement, or removal.
   *
   * @param change raw section change
   * @return readable section change
   */
  @Override
  public Result summarize(TemplateChange change) {
    var subject = SummaryStrategySupport.subjectName(change.path());
    var description = switch (change) {
      case TemplateChange.Added added ->
        "Added " + subject + ": "
            + SummaryStrategySupport.displayValue(added.value()) + ".";
      case TemplateChange.Replaced replaced ->
        SummaryStrategySupport.capitalize(subject)
            + " changed from "
            + SummaryStrategySupport.displayValue(replaced.oldValue())
            + " to "
            + SummaryStrategySupport.displayValue(replaced.newValue())
            + ".";
      case TemplateChange.Removed removed ->
        "Removed " + subject + ": "
            + SummaryStrategySupport.displayValue(removed.oldValue()) + ".";
    };

    return new Result(
        SummaryStrategySupport.sectionName(change.path()),
        new SummaryChange(SummaryStrategySupport.kind(change), description, false));
  }
}
