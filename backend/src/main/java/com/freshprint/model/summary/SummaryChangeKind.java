package com.freshprint.model.summary;

/**
 * User-facing categories for summarized template changes.
 */
public enum SummaryChangeKind {
  /** Describes content introduced by the target version. */
  ADDED,

  /** Describes existing content modified by the target version. */
  CHANGED,

  /** Describes content removed by the target version. */
  REMOVED
}
