package com.freshprint.domain.engagement;

/**
 * The result of comparing an engagement baseline with its template.
 */
public enum UpdateStatus {
  /** The engagement already uses the latest template version. */
  CURRENT,

  /** One or more newer template versions are available. */
  PENDING,

  /**
   * The engagement cannot be evaluated because required metadata is unavailable.
   */
  UNKNOWN
}
