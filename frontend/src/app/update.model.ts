/** The response shape used by the Angular excerpt and the design contract. */
export type UpdateStatus = 'CURRENT' | 'PENDING' | 'UNKNOWN';
export type FreshnessState = 'FRESH' | 'STALE';
export type ChangeKind = 'ADDED' | 'CHANGED' | 'REMOVED';

export interface SummaryChange {
  kind: ChangeKind;
  description: string;
  reviewRecommended: boolean;
}

export interface SummaryGroup {
  section: string;
  changes: SummaryChange[];
}

export type Summary =
  | { state: 'AVAILABLE'; generatedAt: string; groups: SummaryGroup[] }
  | { state: 'COMPUTING'; reason: string }
  | { state: 'UNAVAILABLE'; reason: string };

export interface EngagementUpdate {
  engagementId: string;
  name: string;
  template: { id: string; displayName: string };
  status: UpdateStatus;
  statusReason?: string;
  baselineVersion: number;
  targetVersion: number;
  pendingVersionCount: number;
  summary?: Summary;
  freshness: { state: FreshnessState; checkedAt: string };
}

export interface UpdateDecision {
  decision: 'APPLY' | 'DECLINE';
  expectedBaselineVersion: number;
  targetVersion: number;
}

export interface DecisionReceipt {
  operationId: string;
  status: 'ACCEPTED';
}
