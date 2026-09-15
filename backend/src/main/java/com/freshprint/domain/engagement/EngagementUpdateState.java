package com.freshprint.domain.engagement;

import com.freshprint.domain.template.TemplateMetadata;

import java.util.Objects;

/**
 * The known pending-update state of an engagement.
 */
public sealed interface EngagementUpdateState
    permits EngagementUpdateState.Current,
    EngagementUpdateState.Pending,
    EngagementUpdateState.Unknown {

  /**
   * Returns the engagement represented by this state.
   *
   * @return engagement identifier
   */
  String engagementId();

  /**
   * Returns the update status exposed to callers.
   *
   * @return current, pending, or unknown
   */
  UpdateStatus status();

  /**
   * An engagement already using the latest template version.
   *
   * @param engagement known engagement baseline
   * @param template   latest template metadata
   */
  record Current(EngagementBaseline engagement, TemplateMetadata template)
      implements EngagementUpdateState {

    /**
     * Verifies that the engagement and template match and have equal versions.
     */
    public Current {
      requireMatchingTemplate(engagement, template);
      if (engagement.templateVersion() != template.latestVersion()) {
        throw new IllegalArgumentException("current versions must match");
      }
    }

    /**
     * Returns the identifier from the engagement baseline.
     *
     * @return engagement identifier
     */
    @Override
    public String engagementId() {
      return engagement.engagementId();
    }

    /**
     * Identifies this state as current.
     *
     * @return {@link UpdateStatus#CURRENT}
     */
    @Override
    public UpdateStatus status() {
      return UpdateStatus.CURRENT;
    }
  }

  /**
   * An engagement with one or more newer template versions available.
   *
   * @param engagement          known engagement baseline
   * @param template            latest template metadata
   * @param pendingVersionCount number of published versions after the baseline
   */
  record Pending(
      EngagementBaseline engagement,
      TemplateMetadata template,
      int pendingVersionCount)
      implements EngagementUpdateState {

    /**
     * Verifies matching templates, a newer target, and a positive update count.
     */
    public Pending {
      requireMatchingTemplate(engagement, template);
      if (engagement.templateVersion() >= template.latestVersion()) {
        throw new IllegalArgumentException("pending target must be newer than the baseline");
      }
      if (!template.hasVersion(engagement.templateVersion())) {
        throw new IllegalArgumentException("engagement baseline must be a published version");
      }
      if (pendingVersionCount < 1) {
        throw new IllegalArgumentException("pendingVersionCount must be positive");
      }
      if (pendingVersionCount
          != template.countVersionsAfter(engagement.templateVersion())) {
        throw new IllegalArgumentException(
            "pendingVersionCount must match the published template versions");
      }
    }

    /**
     * Returns the identifier from the engagement baseline.
     *
     * @return engagement identifier
     */
    @Override
    public String engagementId() {
      return engagement.engagementId();
    }

    /**
     * Identifies this state as pending.
     *
     * @return {@link UpdateStatus#PENDING}
     */
    @Override
    public UpdateStatus status() {
      return UpdateStatus.PENDING;
    }
  }

  /**
   * An engagement whose template metadata is not available yet.
   *
   * @param engagementId engagement that could not be evaluated
   * @param reason       explanation suitable for logs or an API reason code
   */
  record Unknown(String engagementId, String reason) implements EngagementUpdateState {

    /**
     * Validates the engagement identifier and reason.
     */
    public Unknown {
      Objects.requireNonNull(engagementId, "engagementId must not be null");
      Objects.requireNonNull(reason, "reason must not be null");
      if (engagementId.isBlank()) {
        throw new IllegalArgumentException("engagementId must not be blank");
      }
      if (reason.isBlank()) {
        throw new IllegalArgumentException("reason must not be blank");
      }
    }

    /**
     * Identifies this state as unknown.
     *
     * @return {@link UpdateStatus#UNKNOWN}
     */
    @Override
    public UpdateStatus status() {
      return UpdateStatus.UNKNOWN;
    }
  }

  /**
   * Verifies that a state compares metadata for the same template.
   *
   * @param engagement engagement baseline
   * @param template   latest template metadata
   */
  private static void requireMatchingTemplate(
      EngagementBaseline engagement,
      TemplateMetadata template) {
    Objects.requireNonNull(engagement, "engagement must not be null");
    Objects.requireNonNull(template, "template must not be null");
    if (!engagement.templateId().equals(template.templateId())) {
      throw new IllegalArgumentException("engagement and template IDs must match");
    }
  }
}
