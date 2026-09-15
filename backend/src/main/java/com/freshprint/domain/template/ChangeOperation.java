package com.freshprint.domain.template;

/**
 * Operations used by the custom template-diff format.
 */
public enum ChangeOperation {
  /** Adds a value that is absent from the baseline template. */
  ADD,

  /** Replaces a baseline value with a target value. */
  REPLACE,

  /** Removes a value that exists in the baseline template. */
  REMOVE
}
