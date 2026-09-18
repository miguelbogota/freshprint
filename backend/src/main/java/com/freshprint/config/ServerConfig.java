package com.freshprint.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freshprint.service.summary.ChangeSummaryGenerator;
import com.freshprint.service.summary.PendingUpdateSummaryService;
import com.freshprint.service.port.TemplateDiffProvider;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Local-demo wiring; configure auth and allowed origins before deployment. */
@Configuration
public class ServerConfig {

  @Bean
  ObjectMapper fixtureMapper() {
    return new ObjectMapper();
  }

  @Bean
  PendingUpdateSummaryService summaryService(TemplateDiffProvider diffs) {
    return new PendingUpdateSummaryService(diffs, new ChangeSummaryGenerator(Clock.systemUTC()));
  }

  @Bean
  WebMvcConfigurer localCors() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**").allowedOrigins("http://localhost:4200")
            .allowedMethods("GET", "POST");
      }
    };
  }
}
