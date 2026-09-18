CREATE TABLE IF NOT EXISTS engagements (
  engagement_id VARCHAR(80) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  template_id VARCHAR(80) NOT NULL,
  template_version INT NOT NULL,
  declined_target INT
);

CREATE TABLE IF NOT EXISTS operations (
  operation_id VARCHAR(80) PRIMARY KEY,
  engagement_id VARCHAR(80) NOT NULL,
  decision VARCHAR(12) NOT NULL,
  baseline_version INT NOT NULL,
  target_version INT NOT NULL,
  status VARCHAR(16) NOT NULL,
  message VARCHAR(255),
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  completed_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_operations_engagement_status
  ON operations (engagement_id, status);
