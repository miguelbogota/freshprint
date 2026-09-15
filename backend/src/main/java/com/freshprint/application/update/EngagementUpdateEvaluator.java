package com.freshprint.application.update;

import com.freshprint.application.port.TemplateCatalog;
import com.freshprint.domain.engagement.EngagementBaseline;
import com.freshprint.domain.engagement.EngagementUpdateState;

import java.util.Objects;

/**
 * Determines whether an engagement is using the latest template version.
 */
public final class EngagementUpdateEvaluator {

  private final TemplateCatalog templateCatalog;

  /**
   * Creates an evaluator backed by the supplied template catalog.
   *
   * @param templateCatalog source of the latest template metadata
   */
  public EngagementUpdateEvaluator(TemplateCatalog templateCatalog) {
    this.templateCatalog = Objects.requireNonNull(
        templateCatalog,
        "templateCatalog must not be null");
  }

  /**
   * Compares an engagement's baseline with the latest published template.
   *
   * @param engagement engagement to evaluate
   * @return current, pending, or unknown update state
   */
  public EngagementUpdateState evaluate(EngagementBaseline engagement) {
    Objects.requireNonNull(engagement, "engagement must not be null");

    var latestTemplate = templateCatalog.findLatest(engagement.templateId());
    if (latestTemplate.isEmpty()) {
      return new EngagementUpdateState.Unknown(
          engagement.engagementId(),
          "TEMPLATE_METADATA_UNAVAILABLE");
    }

    var template = latestTemplate.get();
    if (engagement.templateVersion() == template.latestVersion()) {
      return new EngagementUpdateState.Current(engagement, template);
    }

    if (engagement.templateVersion() < template.latestVersion()) {
      return new EngagementUpdateState.Pending(
          engagement,
          template,
          template.latestVersion() - engagement.templateVersion());
    }

    return new EngagementUpdateState.Unknown(
        engagement.engagementId(),
        "ENGAGEMENT_VERSION_AHEAD_OF_CATALOG");
  }
}
