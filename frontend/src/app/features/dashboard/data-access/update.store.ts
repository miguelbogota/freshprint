import { HttpErrorResponse } from '@angular/common/http';
import { Injectable, computed, signal } from '@angular/core';
import { DecisionGateway } from '../../../core/services';
import type { EngagementUpdate, UpdateDecision } from './update.model';

type DecisionState = 'IDLE' | 'SENDING' | 'ACCEPTED' | 'SUCCEEDED' | 'FAILED';

/** Owns selection and action state; components only display data and emit intent. */
@Injectable({ providedIn: 'root' })
export class UpdateStore {
  readonly items = signal<readonly EngagementUpdate[]>([]);
  readonly selectedId = signal<string | null>(null);
  readonly loading = signal(true);
  readonly loadError = signal(false);
  readonly message = signal<string | null>(null);
  readonly selected = computed(
    () => this.items().find((item) => item.engagementId === this.selectedId()) ?? null,
  );
  readonly decisionState = signal<DecisionState>('IDLE');
  readonly operationId = signal<string | null>(null);
  readonly canDecide = computed(() => {
    const item = this.selected();
    return (
      item?.status === 'PENDING' &&
      !item.declined &&
      item.summary?.state === 'AVAILABLE' &&
      (this.decisionState() === 'IDLE' || this.decisionState() === 'FAILED')
    );
  });

  constructor(private readonly gateway: DecisionGateway) {}

  async load(): Promise<void> {
    this.loading.set(true);
    this.loadError.set(false);
    try {
      const items = await this.gateway.list();
      this.items.set(items);
      if (!items.some((item) => item.engagementId === this.selectedId())) {
        this.selectedId.set(
          items.find((item) => item.status === 'PENDING')?.engagementId ??
            items[0]?.engagementId ??
            null,
        );
      }
    } catch {
      this.loadError.set(true);
    } finally {
      this.loading.set(false);
    }
  }

  select(engagementId: string): void {
    if (this.decisionState() === 'SENDING') return;
    if (this.selectedId() === engagementId) return;
    if (!this.items().some((item) => item.engagementId === engagementId)) return;

    this.selectedId.set(engagementId);
    this.decisionState.set('IDLE');
    this.operationId.set(null);
    this.message.set(null);
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
      void this.waitForResult(receipt.operationId);
    } catch (error) {
      if (error instanceof HttpErrorResponse && error.status === 409) {
        await this.load();
        this.message.set(
          'The versions changed while you were reviewing. Please check the latest update.',
        );
      } else {
        this.message.set('Could not send your decision. Please try again.');
      }
      this.decisionState.set('FAILED');
    }
  }

  private async waitForResult(operationId: string): Promise<void> {
    try {
      for (let attempt = 0; attempt < 20; attempt++) {
        const operation = await this.gateway.operation(operationId);
        if (operation.status === 'SUCCEEDED') {
          this.decisionState.set('SUCCEEDED');
          this.message.set('Decision completed. Your engagement list is up to date.');
          await this.load();
          return;
        }
        if (operation.status === 'FAILED') {
          this.decisionState.set('FAILED');
          this.message.set(operation.message ?? 'The decision could not be completed.');
          return;
        }
        await new Promise((resolve) => setTimeout(resolve, 700));
      }
      this.message.set('Still processing. Refresh to check the latest status.');
    } catch {
      this.message.set('Could not check the final result. Refresh to try again.');
    }
  }
}
