package com.freshprint.dto;

import java.util.List;
import java.util.Map;

/** The update state and readable summary sent to Angular. */
public record UpdateResponse(
    String engagementId,
    String name,
    TemplateRef template,
    String status,
    String statusReason,
    int baselineVersion,
    int targetVersion,
    int pendingVersionCount,
    Map<String, Object> summary,
    Boolean declined,
    Freshness freshness) {

  public record TemplateRef(String id, String displayName) {
  }

  public record Freshness(String state, String checkedAt) {
  }

  public record ListResponse(List<UpdateResponse> items) {
  }
}
