package com.freshprint.domain.engagement;

import java.util.Objects;

/**
 * The template metadata already known for an engagement.
 *
 * @param engagementId    unique engagement identifier
 * @param name            user-facing engagement name
 * @param templateId      identifier of the template used to create the
 *                        engagement
 * @param templateVersion engagement's current template baseline
 */
public record EngagementBaseline(
    String engagementId,
    String name,
    String templateId,
    int templateVersion) {

  /**
   * Validates the engagement identity and template baseline.
   */
  public EngagementBaseline {
    engagementId = requireText(engagementId, "engagementId");
    name = requireText(name, "name");
    templateId = requireText(templateId, "templateId");

    if (templateVersion < 1) {
      throw new IllegalArgumentException("templateVersion must be positive");
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
