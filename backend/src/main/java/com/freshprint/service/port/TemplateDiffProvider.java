package com.freshprint.service.port;

import com.freshprint.model.template.TemplateDiff;

import java.util.Optional;

/**
 * Provides the effective raw diff between two versions of one template.
 */
@FunctionalInterface
public interface TemplateDiffProvider {

  /**
   * Finds the effective diff from an engagement baseline to a newer target.
   *
   * @param templateId  template being compared
   * @param fromVersion baseline version
   * @param toVersion   target version
   * @return effective diff, or empty when it is not available
   */
  Optional<TemplateDiff> findDiff(String templateId, int fromVersion, int toVersion);
}
