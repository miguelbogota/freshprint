package com.freshprint.repository;

import com.freshprint.model.engagement.EngagementEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Spring Data persistence for indexed engagement metadata. */
public interface EngagementJpaRepository extends JpaRepository<EngagementEntity, String> {

  List<EngagementEntity> findAllByOrderByNameAsc();

  @Modifying(clearAutomatically = true)
  @Query("update EngagementEntity e set e.templateVersion = :target, e.declinedTarget = null "
      + "where e.engagementId = :id and e.templateVersion = :baseline")
  int apply(@Param("id") String id, @Param("baseline") int baseline,
      @Param("target") int target);

  @Modifying(clearAutomatically = true)
  @Query("update EngagementEntity e set e.declinedTarget = :target "
      + "where e.engagementId = :id and e.templateVersion = :baseline")
  int decline(@Param("id") String id, @Param("baseline") int baseline,
      @Param("target") int target);
}
