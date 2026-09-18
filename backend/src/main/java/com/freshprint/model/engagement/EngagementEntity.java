package com.freshprint.model.engagement;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The indexed engagement metadata stored in H2; not the full engagement file.
 */
@Entity
@Table(name = "engagements")
public class EngagementEntity {

  @Id
  @Column(name = "engagement_id", length = 80)
  private String engagementId;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "template_id", nullable = false, length = 80)
  private String templateId;

  @Column(name = "template_version", nullable = false)
  private int templateVersion;

  @Column(name = "declined_target")
  private Integer declinedTarget;

  protected EngagementEntity() {
  }

  public EngagementEntity(String engagementId, String name, String templateId,
      int templateVersion) {
    this.engagementId = engagementId;
    this.name = name;
    this.templateId = templateId;
    this.templateVersion = templateVersion;
  }

  public String getEngagementId() {
    return engagementId;
  }

  public String getName() {
    return name;
  }

  public String getTemplateId() {
    return templateId;
  }

  public int getTemplateVersion() {
    return templateVersion;
  }

  public Integer getDeclinedTarget() {
    return declinedTarget;
  }

  public EngagementBaseline baseline() {
    return new EngagementBaseline(engagementId, name, templateId, templateVersion);
  }
}
