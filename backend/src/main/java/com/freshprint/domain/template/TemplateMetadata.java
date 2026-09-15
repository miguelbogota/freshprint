package com.freshprint.domain.template;

import java.time.Instant;
import java.util.Objects;

/**
 * The latest published version of a product template.
 *
 * @param templateId    unique template identifier
 * @param displayName   user-facing template name
 * @param latestVersion latest published version number
 * @param publishedAt   time when the latest version was published
 */
public record TemplateMetadata(
    String templateId,
    String displayName,
    int latestVersion,
    Instant publishedAt) {

  /**
   * Validates the template identity and latest published version.
   */
  public TemplateMetadata {
    templateId = requireText(templateId, "templateId");
    displayName = requireText(displayName, "displayName");
    Objects.requireNonNull(publishedAt, "publishedAt must not be null");

    if (latestVersion < 1) {
      throw new IllegalArgumentException("latestVersion must be positive");
    }
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
