import { Injectable, computed, signal } from '@angular/core';
import { DecisionGateway } from './decision.gateway';
import { updateFixture } from './update.fixture';
import type { EngagementUpdate, UpdateDecision } from './update.model';

type DecisionState = 'IDLE' | 'SENDING' | 'ACCEPTED' | 'FAILED';

/** Owns selection and action state; components only display data and emit intent. */
@Injectable({ providedIn: 'root' })
export class UpdateStore {
  readonly items = signal<readonly EngagementUpdate[]>(updateFixture);
  readonly selectedId = signal<string | null>(updateFixture[0]?.engagementId ?? null);
  readonly selected = computed(
    () => this.items().find((item) => item.engagementId === this.selectedId()) ?? null,
  );
  readonly decisionState = signal<DecisionState>('IDLE');
  readonly operationId = signal<string | null>(null);

  constructor(private readonly gateway: DecisionGateway) {}

  select(engagementId: string): void {
    if (this.decisionState() === 'SENDING') return;
    if (!this.items().some((item) => item.engagementId === engagementId)) return;

    this.selectedId.set(engagementId);
    this.decisionState.set('IDLE');
    this.operationId.set(null);
  }

  canDecide(): boolean {
    const item = this.selected();
    return (
      item?.status === 'PENDING' &&
      item.summary?.state === 'AVAILABLE' &&
      (this.decisionState() === 'IDLE' || this.decisionState() === 'FAILED')
    );
  }

  async decide(decision: UpdateDecision['decision']): Promise<void> {
    if (!this.canDecide()) return;
    const item = this.selected();
    if (!item) return;

    this.decisionState.set('SENDING');
    const request: UpdateDecision = {
      decision,
      expectedBaselineVersion: item.baselineVersion,
      targetVersion: item.targetVersion,
    };

    try {
      const receipt = await this.gateway.submit(item.engagementId, request);
      this.operationId.set(receipt.operationId);
      this.decisionState.set('ACCEPTED');
    } catch {
      this.decisionState.set('FAILED');
    }
  }
}
