package com.freshprint.domain.template;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * The raw effective diff between two versions of one template.
 *
 * @param templateId  template being compared
 * @param fromVersion engagement's baseline version
 * @param toVersion   newer target version
 * @param generatedAt time when the diff was generated
 * @param changes     raw changes between the two versions
 */
public record TemplateDiff(
    String templateId,
    int fromVersion,
    int toVersion,
    Instant generatedAt,
    List<TemplateChange> changes) {

  /**
   * Validates the version range and makes the changes list immutable.
   */
  public TemplateDiff {
    Objects.requireNonNull(templateId, "templateId must not be null");
    Objects.requireNonNull(generatedAt, "generatedAt must not be null");
    changes = List.copyOf(changes);

    if (templateId.isBlank()) {
      throw new IllegalArgumentException("templateId must not be blank");
    }
    if (fromVersion < 1) {
      throw new IllegalArgumentException("fromVersion must be positive");
    }
    if (toVersion <= fromVersion) {
      throw new IllegalArgumentException("toVersion must be newer than fromVersion");
    }
  }
}
