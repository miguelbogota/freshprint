package com.freshprint.repository;

import com.freshprint.model.engagement.EngagementBaseline;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/** Converts indexed JPA entities into the original immutable domain values. */
@Repository
public class EngagementRepository {

  private final EngagementJpaRepository jpa;

  public EngagementRepository(EngagementJpaRepository jpa) {
    this.jpa = jpa;
  }

  public List<EngagementBaseline> all() {
    return jpa.findAllByOrderByNameAsc().stream().map(entity -> entity.baseline()).toList();
  }

  public Optional<EngagementBaseline> find(String id) {
    return jpa.findById(id).map(entity -> entity.baseline());
  }

  public Integer declinedTarget(String id) {
    return jpa.findById(id).map(entity -> entity.getDeclinedTarget()).orElse(null);
  }

  /** Compare-and-set keeps an async decision from updating a changed baseline. */
  @Transactional
  public boolean apply(String id, int baseline, int target) {
    return jpa.apply(id, baseline, target) == 1;
  }

  @Transactional
  public boolean decline(String id, int baseline, int target) {
    return jpa.decline(id, baseline, target) == 1;
  }
}
