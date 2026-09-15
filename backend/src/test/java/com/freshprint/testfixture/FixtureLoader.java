package com.freshprint.testfixture;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshprint.domain.engagement.EngagementBaseline;
import com.freshprint.domain.template.TemplateChange;
import com.freshprint.domain.template.TemplateDiff;
import com.freshprint.domain.template.TemplateMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;

/**
 * Loads the shared JSON fixtures into domain objects for tests.
 */
public final class FixtureLoader {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * Prevents construction of this test utility.
   */
  private FixtureLoader() {
  }

  /**
   * Loads one engagement from {@code engagements.json}.
   *
   * @param engagementId engagement to find
   * @return matching engagement baseline
   * @throws IOException when the fixture cannot be read
   */
  public static EngagementBaseline engagement(String engagementId) throws IOException {
    for (var node : read("engagements.json")) {
      if (engagementId.equals(node.get("engagementId").textValue())) {
        return new EngagementBaseline(
            node.get("engagementId").textValue(),
            node.get("name").textValue(),
            node.get("templateId").textValue(),
            node.get("templateVersion").intValue());
      }
    }
    throw new IllegalArgumentException("Engagement fixture not found: " + engagementId);
  }

  /**
   * Loads the latest metadata for one template from {@code templates.json}.
   *
   * @param templateId template to find
   * @return matching template metadata
   * @throws IOException when the fixture cannot be read
   */
  public static TemplateMetadata template(String templateId) throws IOException {
    for (var node : read("templates.json")) {
      if (templateId.equals(node.get("templateId").textValue())) {
        var latestVersion = node.get("latestVersion").intValue();
        return new TemplateMetadata(
            node.get("templateId").textValue(),
            node.get("displayName").textValue(),
            latestVersion,
            publishedAt(node.get("versions"), latestVersion));
      }
    }
    throw new IllegalArgumentException("Template fixture not found: " + templateId);
  }

  /**
   * Loads a raw template diff fixture into the domain model.
   *
   * @param resourceName diff fixture filename
   * @return parsed template diff
   * @throws IOException when the fixture cannot be read
   */
  public static TemplateDiff diff(String resourceName) throws IOException {
    var node = read(resourceName);
    var changes = new ArrayList<TemplateChange>();
    for (var change : node.get("changes")) {
      changes.add(toTemplateChange(change));
    }

    return new TemplateDiff(
        node.get("templateId").textValue(),
        node.get("fromVersion").intValue(),
        node.get("toVersion").intValue(),
        Instant.parse(node.get("generatedAt").textValue()),
        changes);
  }

  /**
   * Finds the publication time for the latest template version.
   *
   * @param versions      published version nodes
   * @param latestVersion version to find
   * @return matching publication time
   */
  private static Instant publishedAt(JsonNode versions, int latestVersion) {
    for (var version : versions) {
      if (version.get("version").intValue() == latestVersion) {
        return Instant.parse(version.get("publishedAt").textValue());
      }
    }
    throw new IllegalArgumentException("Latest template version is missing");
  }

  /**
   * Converts one custom JSON diff operation into a domain change.
   *
   * @param node raw change node
   * @return matching domain change
   */
  private static TemplateChange toTemplateChange(JsonNode node) {
    var path = node.get("path").textValue();
    return switch (node.get("op").textValue()) {
      case "add" -> new TemplateChange.Added(path, value(node.get("value")));
      case "replace" -> new TemplateChange.Replaced(
          path,
          value(node.get("oldValue")),
          value(node.get("newValue")));
      case "remove" -> new TemplateChange.Removed(path, value(node.get("oldValue")));
      default -> throw new IllegalArgumentException(
          "Unsupported fixture operation: " + node.get("op").textValue());
    };
  }

  /**
   * Converts a JSON value into regular Java maps, lists, and scalar values.
   *
   * @param node JSON value
   * @return plain Java value
   */
  private static Object value(JsonNode node) {
    return OBJECT_MAPPER.convertValue(node, Object.class);
  }

  /**
   * Reads one JSON resource from the test classpath.
   *
   * @param resourceName fixture filename
   * @return parsed JSON root
   * @throws IOException when the fixture cannot be read
   */
  private static JsonNode read(String resourceName) throws IOException {
    try (InputStream stream = FixtureLoader.class
        .getClassLoader()
        .getResourceAsStream(resourceName)) {
      if (stream == null) {
        throw new IllegalArgumentException("Fixture resource not found: " + resourceName);
      }
      return OBJECT_MAPPER.readTree(stream);
    }
  }
}
