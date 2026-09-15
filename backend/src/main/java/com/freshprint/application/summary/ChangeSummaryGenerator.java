package com.freshprint.application.summary;

import com.freshprint.domain.summary.ChangeSummary;
import com.freshprint.domain.summary.SummaryChange;
import com.freshprint.domain.summary.SummaryChangeKind;
import com.freshprint.domain.summary.SummaryGroup;
import com.freshprint.domain.template.TemplateChange;
import com.freshprint.domain.template.TemplateDiff;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Converts a raw diff into a short summary for a non-technical user. */
public final class ChangeSummaryGenerator {

  private final Clock clock;

  /**
   * Creates a generator with a clock for predictable timestamps.
   *
   * @param clock source of the summary timestamp
   */
  public ChangeSummaryGenerator(Clock clock) {
    this.clock = Objects.requireNonNull(clock);
  }

  /**
   * Groups and describes every change in a baseline-to-latest diff.
   *
   * @param diff effective template diff
   * @return readable summary
   */
  public ChangeSummary generate(TemplateDiff diff) {
    Objects.requireNonNull(diff);
    Map<String, List<SummaryChange>> sections = new LinkedHashMap<>();
    for (var change : diff.changes()) {
      var path = change.path();
      var parts = path.split("/");
      var known = parts.length > 2 && path.startsWith("/sections/");
      var section = known ? humanize(parts[2]) : "Other changes";
      var subject = path.contains("/questions/")
          ? "question"
          : humanize(path.substring(path.lastIndexOf('/') + 1));
      var description = switch (change) {
        case TemplateChange.Added added ->
          "Added " + subject + ": " + display(added.value()) + ".";
        case TemplateChange.Replaced replaced ->
          subject + " changed from " + display(replaced.oldValue())
              + " to " + display(replaced.newValue()) + ".";
        case TemplateChange.Removed removed ->
          "Removed " + subject + ": " + display(removed.oldValue()) + ".";
      };
      if (!known) {
        description = "Template content changed at " + path + ".";
      }
      var kind = switch (change) {
        case TemplateChange.Added _ -> SummaryChangeKind.ADDED;
        case TemplateChange.Replaced _ -> SummaryChangeKind.CHANGED;
        case TemplateChange.Removed _ -> SummaryChangeKind.REMOVED;
      };
      sections.computeIfAbsent(section, ignored -> new ArrayList<>())
          .add(new SummaryChange(kind, description, !known));
    }
    var groups = sections.entrySet().stream()
        .map(entry -> new SummaryGroup(entry.getKey(), entry.getValue()))
        .toList();
    return new ChangeSummary(Instant.now(clock), groups);
  }

  /**
   * Turns a JSON value into a compact label or scalar.
   *
   * @param value raw diff value
   * @return readable value
   */
  private static String display(Object value) {
    if (value instanceof Map<?, ?> map && map.get("label") != null) {
      return "\"" + map.get("label") + "\"";
    }
    if (value instanceof String text) {
      return "\"" + text + "\"";
    }
    return String.valueOf(value);
  }

  /**
   * Replaces common JSON-name separators with spaces.
   *
   * @param name path segment
   * @return readable name
   */
  private static String humanize(String name) {
    var words = name.replaceAll("([a-z])([A-Z])", "$1 $2")
        .replace('-', ' ').replace('_', ' ').toLowerCase();
    return words.isEmpty() ? "content"
        : Character.toUpperCase(words.charAt(0))
            + words.substring(1);
  }
}
