package com.freshprint.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.freshprint.model.engagement.EngagementEntity;
import com.freshprint.service.decision.DecisionCompletionService;
import jakarta.persistence.EntityManager;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Checks that JPA writes persist and a failed decision cannot partially apply.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:persistence-test;DB_CLOSE_DELAY=-1")
class JpaPersistenceIntegrationTest {

  @Autowired
  EngagementRepository engagements;

  @Autowired
  EngagementJpaRepository engagementJpa;

  @Autowired
  OperationRepository operations;

  @Autowired
  DecisionCompletionService completion;

  @Autowired
  EntityManager entityManager;

  @Test
  @Transactional
  void staleBaselineCannotOverwriteAndApplyClearsDecline() {
    engagementJpa.save(new EngagementEntity("ENG-JPA-TEST", "JPA test", "AUDIT-CA", 4));

    assertFalse(engagements.apply("ENG-JPA-TEST", 3, 5));
    assertTrue(engagements.decline("ENG-JPA-TEST", 4, 5));
    entityManager.flush();
    entityManager.clear();
    assertEquals(4, engagements.find("ENG-JPA-TEST").orElseThrow().templateVersion());
    assertEquals(5, engagements.declinedTarget("ENG-JPA-TEST"));

    assertTrue(engagements.apply("ENG-JPA-TEST", 4, 5));
    entityManager.flush();
    entityManager.clear();
    assertEquals(5, engagements.find("ENG-JPA-TEST").orElseThrow().templateVersion());
    assertEquals(null, engagements.declinedTarget("ENG-JPA-TEST"));
  }

  @Test
  @Transactional
  void operationStateSurvivesFlushAndReload() {
    operations.create("OP-JPA-TEST", "ENG-1007", "APPLY", 6, 8);
    operations.running("OP-JPA-TEST");
    operations.completed("OP-JPA-TEST", "SUCCEEDED", null);
    entityManager.flush();
    entityManager.clear();

    var saved = operations.find("OP-JPA-TEST").orElseThrow();
    assertEquals("SUCCEEDED", saved.status());
    assertEquals("ENG-1007", saved.engagementId());
    assertTrue(saved.completedAt() != null);
  }

  @Test
  @Transactional
  void restartRecoveryOnlyFailsUnfinishedOperations() {
    operations.create("OP-ACCEPTED-TEST", "ENG-1007", "APPLY", 6, 8);
    operations.create("OP-RUNNING-TEST", "ENG-1003", "DECLINE", 3, 5);
    operations.running("OP-RUNNING-TEST");
    operations.create("OP-DONE-TEST", "ENG-1006", "APPLY", 7, 8);
    operations.completed("OP-DONE-TEST", "SUCCEEDED", null);

    operations.failInterrupted();
    entityManager.flush();
    entityManager.clear();

    assertEquals("FAILED", operations.find("OP-ACCEPTED-TEST").orElseThrow().status());
    assertEquals("FAILED", operations.find("OP-RUNNING-TEST").orElseThrow().status());
    assertEquals("SUCCEEDED", operations.find("OP-DONE-TEST").orElseThrow().status());
  }

  @Test
  void failedOperationWriteRollsBackEngagementUpdate() {
    assertThrows(NoSuchElementException.class,
        () -> completion.complete("OP-NOT-FOUND", "ENG-1007", "APPLY", 6, 8));
    assertEquals(6, engagements.find("ENG-1007").orElseThrow().templateVersion());
  }
}
