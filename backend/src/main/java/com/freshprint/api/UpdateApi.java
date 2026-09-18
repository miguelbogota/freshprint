package com.freshprint.api;

import com.freshprint.application.summary.PendingUpdateSummaryService;
import com.freshprint.application.update.EngagementUpdateEvaluator;
import com.freshprint.domain.engagement.UpdateStatus;
import com.freshprint.domain.summary.ChangeSummaryResult;
import com.freshprint.infrastructure.EngagementRepository;
import com.freshprint.infrastructure.FixtureCatalog;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Read API backed by the metadata index and shared template summaries. */
@RestController
@RequestMapping("/api")
public class UpdateApi {

  private final EngagementRepository engagements;
  private final FixtureCatalog templates;
  private final PendingUpdateSummaryService summaries;
  private final EngagementUpdateEvaluator evaluator;
  private final Map<String, ChangeSummaryResult> summaryCache = new ConcurrentHashMap<>();

  public UpdateApi(EngagementRepository engagements, FixtureCatalog templates,
      PendingUpdateSummaryService summaries) {
    this.engagements = engagements;
    this.templates = templates;
    this.summaries = summaries;
    this.evaluator = new EngagementUpdateEvaluator(templates);
  }

  /**
   * Precomputes summaries for baselines currently in use; requests fill later
   * cache misses.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void warmSummaries() {
    engagements.all().forEach(this::view);
  }

  @GetMapping("/engagements/template-updates")
  public Map<String, Object> list() {
    return Map.of("items", engagements.all().stream().map(this::view).toList());
  }

  @GetMapping("/engagements/{id}/template-update")
  public Map<String, Object> one(@PathVariable("id") String id) {
    return engagements.find(id).map(this::view)
        .orElseThrow(() -> new ApiError(HttpStatus.NOT_FOUND, "ENGAGEMENT_NOT_FOUND"));
  }

  private Map<String, Object> view(com.freshprint.domain.engagement.EngagementBaseline engagement) {
    var state = evaluator.evaluate(engagement);
    var template = templates.findLatest(engagement.templateId());
    var result = new java.util.LinkedHashMap<String, Object>();
    result.put("engagementId", engagement.engagementId());
    result.put("name", engagement.name());
    result.put("template", Map.of("id", engagement.templateId(), "displayName",
        template.map(com.freshprint.domain.template.TemplateMetadata::displayName)
            .orElse(engagement.templateId())));
    result.put("status", state.status().name());
    if (state.reason() != null)
      result.put("statusReason", state.reason());
    result.put("baselineVersion", engagement.templateVersion());
    result.put("targetVersion", state.targetVersion());
    result.put("pendingVersionCount", state.pendingVersionCount());
    if (state.status() == UpdateStatus.PENDING) {
      var key = engagement.templateId() + ":" + engagement.templateVersion() + ":" + state.targetVersion();
      var summary = summaryCache.computeIfAbsent(key, ignored -> summaries.summarize(state));
      result.put("summary", summaryView(summary));
      result.put("declined", Integer.valueOf(state.targetVersion())
          .equals(engagements.declinedTarget(engagement.engagementId())));
    }
    result.put("freshness", Map.of("state", "FRESH", "checkedAt", Instant.now().toString()));
    return result;
  }

  private Map<String, Object> summaryView(ChangeSummaryResult summary) {
    return switch (summary) {
      case ChangeSummaryResult.Available available -> Map.of("state", "AVAILABLE",
          "generatedAt", available.summary().generatedAt().toString(),
          "groups", available.summary().groups());
      case ChangeSummaryResult.Computing computing -> Map.of("state", "COMPUTING",
          "reason", computing.reason());
      case ChangeSummaryResult.Unavailable unavailable -> Map.of("state", "UNAVAILABLE",
          "reason", unavailable.reason());
    };
  }
}
