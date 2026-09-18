package com.freshprint.repository;

import com.freshprint.model.engagement.OperationEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data persistence for asynchronous decision operations. */
public interface OperationJpaRepository extends JpaRepository<OperationEntity, String> {

  boolean existsByEngagementIdAndStatusIn(String engagementId, Collection<String> statuses);

  List<OperationEntity> findAllByStatusIn(Collection<String> statuses);
}
