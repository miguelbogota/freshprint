package com.freshprint.application.summary.strategy;

import com.freshprint.domain.summary.SummaryChangeKind;
import com.freshprint.domain.template.TemplateChange;

import java.util.Map;

/**
 * Shared text helpers used by the built-in summary strategies.
 */
final class SummaryStrategySupport {

  /**
   * Prevents construction of this utility class.
   */
  private SummaryStrategySupport() {
  }

  /**
   * Extracts and formats the section name from a JSON path.
   *
   * @param path raw template path
   * @return readable section name
   */
  static String sectionName(String path) {
    var parts = path.split("/");
    return parts.length > 2 ? capitalize(humanize(parts[2])) : "Other changes";
  }

  /**
   * Extracts and formats the final named part of a JSON path.
   *
   * @param path raw template path
   * @return readable subject name
   */
  static String subjectName(String path) {
    var parts = path.split("/");
    for (var index = parts.length - 1; index >= 0; index--) {
      if (!parts[index].isBlank() && !parts[index].chars().allMatch(Character::isDigit)) {
        return humanize(parts[index]);
      }
    }
    return "content";
  }

  /**
   * Chooses a compact value suitable for a user-facing sentence.
   *
   * @param value raw diff value
   * @return readable representation
   */
  static String displayValue(Object value) {
    if (value instanceof Map<?, ?> map && map.get("label") != null) {
      return quote(map.get("label"));
    }
    if (value instanceof String text) {
      return quote(text);
    }
    return String.valueOf(value);
  }

  /**
   * Maps a raw change type to its user-facing category.
   *
   * @param change raw template change
   * @return matching summary kind
   */
  static SummaryChangeKind kind(TemplateChange change) {
    return switch (change) {
      case TemplateChange.Added _ -> SummaryChangeKind.ADDED;
      case TemplateChange.Replaced _ -> SummaryChangeKind.CHANGED;
      case TemplateChange.Removed _ -> SummaryChangeKind.REMOVED;
    };
  }

  /**
   * Converts camel case, underscores, and hyphens into normal words.
   *
   * @param value technical name
   * @return readable lower-case name
   */
  static String humanize(String value) {
    return value
        .replaceAll("([a-z])([A-Z])", "$1 $2")
        .replace('-', ' ')
        .replace('_', ' ')
        .toLowerCase();
  }

  /**
   * Capitalizes the first character of a non-empty value.
   *
   * @param value value to capitalize
   * @return capitalized value
   */
  static String capitalize(String value) {
    if (value.isEmpty()) {
      return value;
    }
    return Character.toUpperCase(value.charAt(0)) + value.substring(1);
  }

  /**
   * Wraps a value in quotation marks.
   *
   * @param value value to quote
   * @return quoted value
   */
  private static String quote(Object value) {
    return "\"" + value + "\"";
  }
}
