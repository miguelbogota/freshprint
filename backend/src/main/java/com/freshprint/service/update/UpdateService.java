package com.freshprint.service.update;

import com.freshprint.dto.UpdateResponse;
import com.freshprint.dto.UpdateResponse.Freshness;
import com.freshprint.dto.UpdateResponse.TemplateRef;
import com.freshprint.exception.ApiException;
import com.freshprint.model.engagement.EngagementBaseline;
import com.freshprint.model.engagement.UpdateStatus;
import com.freshprint.model.summary.ChangeSummaryResult;
import com.freshprint.model.template.TemplateMetadata;
import com.freshprint.repository.EngagementRepository;
import com.freshprint.repository.FixtureCatalog;
import com.freshprint.service.summary.PendingUpdateSummaryService;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Builds fast update views from indexed engagement metadata and shared template
 * data.
 */
@Service
public class UpdateService {

  private final EngagementRepository engagements;
  private final FixtureCatalog templates;
  private final PendingUpdateSummaryService summaries;
  private final EngagementUpdateEvaluator evaluator;
  private final Map<String, ChangeSummaryResult> summaryCache = new ConcurrentHashMap<>();

  public UpdateService(EngagementRepository engagements, FixtureCatalog templates,
      PendingUpdateSummaryService summaries) {
    this.engagements = engagements;
    this.templates = templates;
    this.summaries = summaries;
    this.evaluator = new EngagementUpdateEvaluator(templates);
  }

  /**
   * Precompute summaries for current baselines; requests fill later cache misses.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void warmSummaries() {
    engagements.all().forEach(this::view);
  }

  public UpdateResponse.ListResponse list() {
    return new UpdateResponse.ListResponse(engagements.all().stream().map(this::view).toList());
  }

  public UpdateResponse one(String id) {
    return engagements.find(id).map(this::view)
        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ENGAGEMENT_NOT_FOUND"));
  }

  private UpdateResponse view(EngagementBaseline engagement) {
    var state = evaluator.evaluate(engagement);
    var templateName = templates.findLatest(engagement.templateId())
        .map(TemplateMetadata::displayName).orElse(engagement.templateId());
    Map<String, Object> summary = null;
    Boolean declined = null;
    if (state.status() == UpdateStatus.PENDING) {
      var key = engagement.templateId() + ":" + engagement.templateVersion() + ":" + state.targetVersion();
      summary = summaryView(summaryCache.computeIfAbsent(key, ignored -> summaries.summarize(state)));
      declined = Integer.valueOf(state.targetVersion())
          .equals(engagements.declinedTarget(engagement.engagementId()));
    }
    return new UpdateResponse(engagement.engagementId(), engagement.name(),
        new TemplateRef(engagement.templateId(), templateName), state.status().name(),
        state.reason(), engagement.templateVersion(), state.targetVersion(),
        state.pendingVersionCount(), summary, declined,
        new Freshness("FRESH", Instant.now().toString()));
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
