package com.freshprint.application.summary;

import com.freshprint.application.port.TemplateDiffProvider;
import com.freshprint.domain.engagement.EngagementUpdateState;
import com.freshprint.domain.summary.ChangeSummaryResult;

import java.util.Objects;

/**
 * Creates the effective summary for an engagement with a pending update.
 */
public final class PendingUpdateSummaryService {

  private final TemplateDiffProvider diffProvider;
  private final ChangeSummaryGenerator summaryGenerator;

  /**
   * Creates a service backed by a diff provider and summary generator.
   *
   * @param diffProvider source of baseline-to-target template diffs
   * @param summaryGenerator converter for raw template changes
   */
  public PendingUpdateSummaryService(
      TemplateDiffProvider diffProvider,
      ChangeSummaryGenerator summaryGenerator) {
    this.diffProvider = Objects.requireNonNull(diffProvider, "diffProvider must not be null");
    this.summaryGenerator = Objects.requireNonNull(
        summaryGenerator,
        "summaryGenerator must not be null");
  }

  /**
   * Loads and summarizes the direct diff from the engagement baseline to the
   * latest template version.
   *
   * @param pending pending engagement update
   * @return available summary or an explicit unavailable result
   */
  public ChangeSummaryResult summarize(EngagementUpdateState.Pending pending) {
    Objects.requireNonNull(pending, "pending must not be null");

    var engagement = pending.engagement();
    var template = pending.template();
    var diff = diffProvider.findDiff(
        engagement.templateId(),
        engagement.templateVersion(),
        template.latestVersion());

    if (diff.isEmpty()) {
      return new ChangeSummaryResult.Unavailable("TEMPLATE_DIFF_UNAVAILABLE");
    }

    var templateDiff = diff.get();
    if (!templateDiff.templateId().equals(engagement.templateId())
        || templateDiff.fromVersion() != engagement.templateVersion()
        || templateDiff.toVersion() != template.latestVersion()) {
      return new ChangeSummaryResult.Unavailable("TEMPLATE_DIFF_MISMATCH");
    }

    return new ChangeSummaryResult.Available(summaryGenerator.generate(templateDiff));
  }
}
