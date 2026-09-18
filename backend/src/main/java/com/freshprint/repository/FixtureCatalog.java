package com.freshprint.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshprint.service.port.TemplateCatalog;
import com.freshprint.service.port.TemplateDiffProvider;
import com.freshprint.model.template.TemplateChange;
import com.freshprint.model.template.TemplateDiff;
import com.freshprint.model.template.TemplateMetadata;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * Loads the supplied template fixtures once, without opening engagement files
 * on requests.
 */
@Component
public class FixtureCatalog implements TemplateCatalog, TemplateDiffProvider {

  private final Map<String, TemplateMetadata> templates = new HashMap<>();
  private final Map<String, TemplateDiff> diffs = new HashMap<>();

  public FixtureCatalog(ObjectMapper mapper) throws IOException {
    try (var input = new ClassPathResource("fixtures/templates.json").getInputStream()) {
      for (var node : mapper.readTree(input)) {
        var versions = new ArrayList<Integer>();
        Instant publishedAt = null;
        for (var version : node.path("versions")) {
          versions.add(version.path("version").asInt());
          publishedAt = Instant.parse(version.path("publishedAt").asText());
        }
        var metadata = new TemplateMetadata(node.path("templateId").asText(),
            node.path("displayName").asText(), node.path("latestVersion").asInt(),
            publishedAt, versions);
        templates.put(metadata.templateId(), metadata);
      }
    }
    var resolver = new PathMatchingResourcePatternResolver();
    for (var resource : resolver.getResources("classpath:fixtures/template-diff-*.json")) {
      try (var input = resource.getInputStream()) {
        var node = mapper.readTree(input);
        var changes = new ArrayList<TemplateChange>();
        for (var entry : node.path("changes")) {
          var path = entry.path("path").asText();
          changes.add(switch (entry.path("op").asText()) {
            case "add" -> new TemplateChange.Added(path, value(mapper, entry.path("value")));
            case "replace" -> new TemplateChange.Replaced(path,
                value(mapper, entry.path("oldValue")), value(mapper, entry.path("newValue")));
            case "remove" -> new TemplateChange.Removed(path, value(mapper, entry.path("oldValue")));
            default -> throw new IllegalArgumentException("Unsupported diff operation");
          });
        }
        var diff = new TemplateDiff(node.path("templateId").asText(),
            node.path("fromVersion").asInt(), node.path("toVersion").asInt(),
            Instant.parse(node.path("generatedAt").asText()), changes);
        diffs.put(key(diff.templateId(), diff.fromVersion(), diff.toVersion()), diff);
      }
    }
  }

  private static Object value(ObjectMapper mapper, JsonNode node) {
    return mapper.convertValue(node, Object.class);
  }

  private static String key(String id, int from, int to) {
    return id + ":" + from + ":" + to;
  }

  @Override
  public Optional<TemplateMetadata> findLatest(String templateId) {
    return Optional.ofNullable(templates.get(templateId));
  }

  @Override
  public Optional<TemplateDiff> findDiff(String templateId, int fromVersion, int toVersion) {
    var direct = diffs.get(key(templateId, fromVersion, toVersion));
    if (direct != null)
      return Optional.of(direct);

    var metadata = templates.get(templateId);
    if (metadata == null || !metadata.hasVersion(fromVersion) || !metadata.hasVersion(toVersion)
        || fromVersion >= toVersion)
      return Optional.empty();

    var versions = metadata.publishedVersions();
    var merged = new LinkedHashMap<String, TemplateChange>();
    Instant generatedAt = null;
    for (int i = versions.indexOf(fromVersion); i < versions.indexOf(toVersion); i++) {
      var step = diffs.get(key(templateId, versions.get(i), versions.get(i + 1)));
      if (step == null)
        return Optional.empty();
      generatedAt = step.generatedAt();
      for (var change : step.changes()) {
        var previous = merged.get(change.path());
        if (previous == null) {
          merged.put(change.path(), change);
        } else {
          var original = previous instanceof TemplateChange.Added ? null
              : previous instanceof TemplateChange.Replaced replaced ? replaced.oldValue()
                  : ((TemplateChange.Removed) previous).oldValue();
          var current = change instanceof TemplateChange.Removed ? null
              : change instanceof TemplateChange.Replaced replaced ? replaced.newValue()
                  : ((TemplateChange.Added) change).value();
          if (original == null && current == null)
            merged.remove(change.path());
          else if (original == null)
            merged.put(change.path(), new TemplateChange.Added(change.path(), current));
          else if (current == null)
            merged.put(change.path(), new TemplateChange.Removed(change.path(), original));
          else
            merged.put(change.path(), new TemplateChange.Replaced(change.path(), original, current));
        }
      }
    }
    return Optional.of(new TemplateDiff(templateId, fromVersion, toVersion,
        generatedAt, List.copyOf(merged.values())));
  }
}
