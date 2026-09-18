package com.freshprint.service.summary;

import com.freshprint.service.update.EngagementUpdateEvaluator;
import com.freshprint.model.summary.ChangeSummaryResult;
import com.freshprint.model.summary.SummaryStatus;
import com.freshprint.testfixture.FixtureDiffProvider;
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
    var reviewTemplate = FixtureLoader.template("REVIEW-CA");
    var auditTemplate = FixtureLoader.template("AUDIT-CA");

    var reviewEvaluator = new EngagementUpdateEvaluator(templateId -> Optional.of(reviewTemplate));
    var auditEvaluator = new EngagementUpdateEvaluator(
        templateId -> Optional.of(auditTemplate));

    var reviewPending = reviewEvaluator.evaluate(FixtureLoader.engagement("ENG-1007"));
    var oneVersionBehind = reviewEvaluator.evaluate(FixtureLoader.engagement("ENG-1006"));
    var auditPending = auditEvaluator.evaluate(FixtureLoader.engagement("ENG-1003"));

    var generator = new ChangeSummaryGenerator(
        Clock.fixed(Instant.parse("2026-08-25T13:04:55Z"), ZoneOffset.UTC));
    var diffProvider = new FixtureDiffProvider();

    var service = new PendingUpdateSummaryService(diffProvider, generator);

    var mismatchedDiff = FixtureLoader.diff("template-diff-review-ca-v7-v8.json");
    var mismatchedService = new PendingUpdateSummaryService(
        (templateId, fromVersion, toVersion) -> Optional.of(mismatchedDiff),
        generator);

    var available = assertInstanceOf(
        ChangeSummaryResult.Available.class,
        service.summarize(reviewPending));
    var versionSevenSummary = assertInstanceOf(
        ChangeSummaryResult.Available.class,
        service.summarize(oneVersionBehind));
    var unavailable = assertInstanceOf(
        ChangeSummaryResult.Unavailable.class,
        service.summarize(auditPending));
    var mismatched = assertInstanceOf(
        ChangeSummaryResult.Unavailable.class,
        mismatchedService.summarize(reviewPending));

    assertAll(
        () -> assertEquals(2, reviewPending.pendingVersionCount()),
        () -> assertEquals(SummaryStatus.AVAILABLE, available.status()),
        () -> assertEquals(4, available.summary().groups().size()),
        () -> assertEquals(3, versionSevenSummary.summary().groups().size()),
        () -> assertEquals(SummaryStatus.UNAVAILABLE, unavailable.status()),
        () -> assertEquals("TEMPLATE_DIFF_UNAVAILABLE", unavailable.reason()),
        () -> assertEquals("TEMPLATE_DIFF_MISMATCH", mismatched.reason()));
  }
}
