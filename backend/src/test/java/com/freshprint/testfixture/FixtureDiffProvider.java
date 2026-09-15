package com.freshprint.testfixture;

import com.freshprint.application.port.TemplateDiffProvider;
import com.freshprint.domain.template.TemplateDiff;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;

/** Picks a matching JSON diff from the shared test fixtures. */
public final class FixtureDiffProvider implements TemplateDiffProvider {

  /**
   * Creates a provider for the available test fixtures.
   */
  public FixtureDiffProvider() {
  }

  /**
   * Looks for a fixture named by template ID and version pair.
   *
   * @param templateId  template being compared
   * @param fromVersion engagement baseline version
   * @param toVersion   latest template version
   * @return matching diff, or empty when no direct fixture exists
   */
  @Override
  public Optional<TemplateDiff> findDiff(
      String templateId, int fromVersion, int toVersion) {
    var templateName = switch (templateId) {
      case "AUDIT-CA" -> "audit-ca";
      case "REVIEW-CA" -> "review-ca";
      case "RISK-CA" -> "risk-ca";
      default -> null;
    };
    if (templateName == null || fromVersion >= toVersion) {
      return Optional.empty();
    }

    var filename = "template-diff-" + templateName
        + "-v" + fromVersion + "-v" + toVersion + ".json";
    if (getClass().getClassLoader().getResource(filename) == null) {
      return Optional.empty();
    }
    try {
      return Optional.of(FixtureLoader.diff(filename));
    } catch (IOException exception) {
      throw new UncheckedIOException("Could not read " + filename, exception);
    }
  }
}
