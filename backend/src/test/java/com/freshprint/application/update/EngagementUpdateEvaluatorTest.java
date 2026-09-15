package com.freshprint.application.update;

import com.freshprint.domain.engagement.EngagementBaseline;
import com.freshprint.domain.engagement.EngagementUpdateState;
import com.freshprint.domain.engagement.UpdateStatus;
import com.freshprint.testfixture.FixtureLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Verifies engagement update evaluation against the latest template metadata.
 */
class EngagementUpdateEvaluatorTest {

  /**
   * Covers current, accumulated pending, missing, and inconsistent metadata
   * using the shared JSON fixtures.
   *
   * @throws IOException when fixture data cannot be loaded
   */
  @Test
  void evaluatesEverySupportedUpdateState() throws IOException {
    var template = FixtureLoader.template("AUDIT-CA");
    var evaluator = new EngagementUpdateEvaluator(templateId -> Optional.of(template));
    var unavailableEvaluator = new EngagementUpdateEvaluator(templateId -> Optional.empty());

    var current = evaluator.evaluate(FixtureLoader.engagement("ENG-1001"));
    var pending = evaluator.evaluate(FixtureLoader.engagement("ENG-1003"));
    var missing = unavailableEvaluator.evaluate(FixtureLoader.engagement("ENG-1002"));
    var ahead = evaluator.evaluate(new EngagementBaseline(
        "ENG-AHEAD",
        "Future Engagement",
        "AUDIT-CA",
        6));

    var pendingState = assertInstanceOf(EngagementUpdateState.Pending.class, pending);
    var missingState = assertInstanceOf(EngagementUpdateState.Unknown.class, missing);
    var aheadState = assertInstanceOf(EngagementUpdateState.Unknown.class, ahead);

    assertAll(
        () -> assertEquals(UpdateStatus.CURRENT, current.status()),
        () -> assertEquals(2, pendingState.pendingVersionCount()),
        () -> assertEquals("TEMPLATE_METADATA_UNAVAILABLE", missingState.reason()),
        () -> assertEquals("ENGAGEMENT_VERSION_AHEAD_OF_CATALOG", aheadState.reason()));
  }

}
