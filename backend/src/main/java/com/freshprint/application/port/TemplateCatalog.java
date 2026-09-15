package com.freshprint.application.port;

import com.freshprint.domain.template.TemplateMetadata;

import java.util.Optional;

/**
 * Provides the latest published metadata for a template.
 */
@FunctionalInterface
public interface TemplateCatalog {

  /**
   * Finds the latest published metadata for a template.
   *
   * @param templateId template to look up
   * @return latest metadata, or empty when the template is unavailable
   */
  Optional<TemplateMetadata> findLatest(String templateId);
}
