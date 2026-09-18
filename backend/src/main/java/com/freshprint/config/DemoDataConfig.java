package com.freshprint.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshprint.model.engagement.EngagementEntity;
import com.freshprint.repository.EngagementJpaRepository;
import com.freshprint.repository.OperationRepository;
import java.io.IOException;
import java.util.ArrayList;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/** Seeds the local demo once and marks interrupted async work as failed on restart. */
@Configuration
public class DemoDataConfig {

  @Bean
  ApplicationRunner seedEngagements(EngagementJpaRepository engagements,
      OperationRepository operations, ObjectMapper mapper) {
    return args -> {
      operations.failInterrupted();
      if (engagements.count() != 0) return;
      var seed = new ArrayList<EngagementEntity>();
      try (var input = new ClassPathResource("fixtures/engagements.json").getInputStream()) {
        for (var node : mapper.readTree(input)) {
          seed.add(new EngagementEntity(node.path("engagementId").asText(),
              node.path("name").asText(), node.path("templateId").asText(),
              node.path("templateVersion").asInt()));
        }
      } catch (IOException exception) {
        throw new IllegalStateException("Could not seed demo engagements", exception);
      }
      engagements.saveAll(seed);
    };
  }
}
