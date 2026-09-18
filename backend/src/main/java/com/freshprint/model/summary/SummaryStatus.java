package com.freshprint.model.summary;

/**
 * Availability of a human-readable change summary.
 */
public enum SummaryStatus {
  /** The summary is ready to display. */
  AVAILABLE,

  /** The summary is being generated asynchronously. */
  COMPUTING,

  /** The summary could not be generated. */
  UNAVAILABLE
}
