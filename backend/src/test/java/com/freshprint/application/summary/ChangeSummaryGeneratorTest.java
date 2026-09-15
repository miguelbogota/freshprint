package com.freshprint.application.summary;

import com.freshprint.domain.summary.SummaryChangeKind;
import com.freshprint.testfixture.FixtureLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies strategy selection and grouping for readable change summaries.
 */
class ChangeSummaryGeneratorTest {

  private static final Instant GENERATED_AT = Instant.parse("2026-08-25T13:04:55Z");

  /**
   * Uses a real multi-version diff and keeps unknown paths visible for review.
   *
   * @throws IOException when fixture data cannot be loaded
   */
  @Test
  void summarizesKnownAndUnknownChanges() throws IOException {
    var generator = ChangeSummaryGenerator.standard(
        Clock.fixed(GENERATED_AT, ZoneOffset.UTC));
    var diff = FixtureLoader.diff("template-diff-review-ca-v6-v8.json");

    var summary = generator.generate(diff);
    var fallback = summary.groups().getFirst().changes().getFirst();
    var question = summary.groups().get(1).changes().getFirst();
    var tolerance = summary.groups().get(2).changes().getFirst();

    assertAll(
        () -> assertEquals(GENERATED_AT, summary.generatedAt()),
        () -> assertEquals(6, diff.fromVersion()),
        () -> assertEquals(8, diff.toVersion()),
        () -> assertEquals(
            List.of("Other changes", "Inquiries", "Analytics", "Completion"),
            summary.groups().stream().map(group -> group.section()).toList()),
        () -> assertEquals(SummaryChangeKind.ADDED, question.kind()),
        () -> assertEquals(
            "Added question \"Describe any events after the reporting date that may require adjustment or disclosure.\".",
            question.description()),
        () -> assertEquals(
            "Tolerance changed from 0.15 to 0.1.",
            tolerance.description()),
        () -> assertFalse(question.reviewRecommended()),
        () -> assertTrue(fallback.reviewRecommended()),
        () -> assertTrue(fallback.description().contains("/metadata/displayName")));
  }
}
