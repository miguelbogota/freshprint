package com.freshprint.application.summary;

import com.freshprint.application.update.EngagementUpdateEvaluator;
import com.freshprint.domain.engagement.EngagementUpdateState;
import com.freshprint.domain.summary.ChangeSummaryResult;
import com.freshprint.domain.summary.SummaryStatus;
import com.freshprint.testfixture.FixtureLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Verifies the complete pending-update summary use case with shared fixtures.
 */
class PendingUpdateSummaryServiceTest {

  /**
   * Uses the direct multi-version diff and reports a missing diff explicitly.
   *
   * @throws IOException when fixture data cannot be loaded
   */
  @Test
  void summarizesAccumulatedVersionsAndHandlesMissingDiff() throws IOException {
    var template = FixtureLoader.template("REVIEW-CA");
    var evaluator = new EngagementUpdateEvaluator(templateId -> Optional.of(template));
    var pending = assertInstanceOf(
        EngagementUpdateState.Pending.class,
        evaluator.evaluate(FixtureLoader.engagement("ENG-1007")));
    var diff = FixtureLoader.diff("template-diff-review-ca-v6-v8.json");
    var generator = ChangeSummaryGenerator.standard(
        Clock.fixed(Instant.parse("2026-08-25T13:04:55Z"), ZoneOffset.UTC));
    var availableService = new PendingUpdateSummaryService(
        (templateId, fromVersion, toVersion) -> Optional.of(diff),
        generator);
    var unavailableService = new PendingUpdateSummaryService(
        (templateId, fromVersion, toVersion) -> Optional.empty(),
        generator);
    var mismatchedDiff = FixtureLoader.diff("template-diff-review-ca-v7-v8.json");
    var mismatchedService = new PendingUpdateSummaryService(
        (templateId, fromVersion, toVersion) -> Optional.of(mismatchedDiff),
        generator);

    var available = assertInstanceOf(
        ChangeSummaryResult.Available.class,
        availableService.summarize(pending));
    var unavailable = assertInstanceOf(
        ChangeSummaryResult.Unavailable.class,
        unavailableService.summarize(pending));
    var mismatched = assertInstanceOf(
        ChangeSummaryResult.Unavailable.class,
        mismatchedService.summarize(pending));

    assertAll(
        () -> assertEquals(2, pending.pendingVersionCount()),
        () -> assertEquals(SummaryStatus.AVAILABLE, available.status()),
        () -> assertEquals(4, available.summary().groups().size()),
        () -> assertEquals(SummaryStatus.UNAVAILABLE, unavailable.status()),
        () -> assertEquals("TEMPLATE_DIFF_UNAVAILABLE", unavailable.reason()),
        () -> assertEquals("TEMPLATE_DIFF_MISMATCH", mismatched.reason()));
  }
}
