package com.freshprint.model.engagement;

import java.util.Objects;

/**
 * A simple result of comparing an engagement with its latest template.
 *
 * @param engagement          engagement being checked
 * @param status              current, pending, or unknown
 * @param targetVersion       latest template version, or zero when unknown
 * @param pendingVersionCount number of newer published versions
 * @param reason              explanation when the status is unknown
 */
public record EngagementUpdateState(
    EngagementBaseline engagement,
    UpdateStatus status,
    int targetVersion,
    int pendingVersionCount,
    String reason) {

  /**
   * Checks that each status has the fields it needs.
   */
  public EngagementUpdateState {
    Objects.requireNonNull(engagement);
    Objects.requireNonNull(status);
    if (status == UpdateStatus.PENDING
        && (targetVersion <= engagement.templateVersion() || pendingVersionCount < 1)) {
      throw new IllegalArgumentException("Pending update needs a newer target");
    }
    if (status == UpdateStatus.CURRENT
        && (targetVersion != engagement.templateVersion() || pendingVersionCount != 0)) {
      throw new IllegalArgumentException("Current versions must match");
    }
    if (status == UpdateStatus.UNKNOWN && (reason == null || reason.isBlank())) {
      throw new IllegalArgumentException("Unknown state needs a reason");
    }
  }
}
