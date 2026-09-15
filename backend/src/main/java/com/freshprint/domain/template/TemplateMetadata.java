package com.freshprint.domain.template;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * The latest published version of a product template.
 *
 * @param templateId        unique template identifier
 * @param displayName       user-facing template name
 * @param latestVersion     latest published version number
 * @param publishedAt       time when the latest version was published
 * @param publishedVersions ordered published version numbers
 */
public record TemplateMetadata(
    String templateId,
    String displayName,
    int latestVersion,
    Instant publishedAt,
    List<Integer> publishedVersions) {

  /**
   * Validates the template identity and latest published version.
   */
  public TemplateMetadata {
    templateId = requireText(templateId, "templateId");
    displayName = requireText(displayName, "displayName");
    Objects.requireNonNull(publishedAt, "publishedAt must not be null");
    publishedVersions = List.copyOf(publishedVersions);

    if (latestVersion < 1) {
      throw new IllegalArgumentException("latestVersion must be positive");
    }
    if (publishedVersions.isEmpty()) {
      throw new IllegalArgumentException("publishedVersions must not be empty");
    }
    if (!publishedVersions.contains(latestVersion)) {
      throw new IllegalArgumentException("publishedVersions must contain latestVersion");
    }
    for (var index = 0; index < publishedVersions.size(); index++) {
      var version = publishedVersions.get(index);
      if (version == null || version < 1) {
        throw new IllegalArgumentException("published versions must be positive");
      }
      if (index > 0 && version <= publishedVersions.get(index - 1)) {
        throw new IllegalArgumentException(
            "publishedVersions must be unique and ordered");
      }
    }
    if (!publishedVersions.getLast().equals(latestVersion)) {
      throw new IllegalArgumentException("latestVersion must be the final published version");
    }
  }

  /**
   * Counts actual published versions newer than an engagement baseline.
   *
   * @param baselineVersion engagement's recorded template version
   * @return number of published versions after the baseline
   */
  public int countVersionsAfter(int baselineVersion) {
    return Math.toIntExact(publishedVersions.stream()
        .filter(version -> version > baselineVersion)
        .count());
  }

  /**
   * Checks whether a version belongs to this template's publication history.
   *
   * @param version version number to check
   * @return whether the version was published
   */
  public boolean hasVersion(int version) {
    return publishedVersions.contains(version);
  }

  /**
   * Returns a required text value after checking that it contains content.
   *
   * @param value value to validate
   * @param field field name used in validation messages
   * @return the validated value
   */
  private static String requireText(String value, String field) {
    Objects.requireNonNull(value, field + " must not be null");
    if (value.isBlank()) {
      throw new IllegalArgumentException(field + " must not be blank");
    }
    return value;
  }
}
