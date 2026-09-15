package com.freshprint.domain.template;

import java.util.Objects;

/**
 * A raw change between two template versions.
 */
public sealed interface TemplateChange
    permits TemplateChange.Added, TemplateChange.Replaced, TemplateChange.Removed {

  /**
   * Returns the path of the changed value inside the template JSON.
   *
   * @return JSON path supplied by the diff provider
   */
  String path();

  /**
   * A value added by the target template version.
   *
   * @param path  location of the new value
   * @param value value introduced by the target version
   */
  record Added(String path, Object value) implements TemplateChange {

    /**
     * Validates the change path.
     */
    public Added {
      path = requirePath(path);
    }

  }

  /**
   * A value replaced between the baseline and target versions.
   *
   * @param path     location of the replaced value
   * @param oldValue value in the baseline version
   * @param newValue value in the target version
   */
  record Replaced(String path, Object oldValue, Object newValue) implements TemplateChange {

    /**
     * Validates the change path.
     */
    public Replaced {
      path = requirePath(path);
    }

  }

  /**
   * A value removed by the target template version.
   *
   * @param path     location of the removed value
   * @param oldValue value present in the baseline version
   */
  record Removed(String path, Object oldValue) implements TemplateChange {

    /**
     * Validates the change path.
     */
    public Removed {
      path = requirePath(path);
    }

  }

  /**
   * Returns a path after checking that it resembles the supplied JSON paths.
   *
   * @param path path to validate
   * @return the validated path
   */
  private static String requirePath(String path) {
    Objects.requireNonNull(path, "path must not be null");
    if (path.isBlank() || !path.startsWith("/")) {
      throw new IllegalArgumentException("path must be a non-blank JSON path");
    }
    return path;
  }
}
