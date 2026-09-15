import { TestBed } from '@angular/core/testing';
import { DecisionGateway } from './decision.gateway';
import type { DecisionReceipt, UpdateDecision } from './update.model';
import { UpdateStore } from './update.store';

describe('UpdateStore', () => {
  it('sends the reviewed versions once and keeps ACCEPTED distinct from applied', async () => {
    const calls: { engagementId: string; request: UpdateDecision }[] = [];
    let finish!: (receipt: DecisionReceipt) => void;
    const pendingReceipt = new Promise<DecisionReceipt>((resolve) => {
      finish = resolve;
    });
    TestBed.configureTestingModule({
      providers: [
        {
          provide: DecisionGateway,
          useValue: {
            submit: (engagementId: string, request: UpdateDecision) => {
              calls.push({ engagementId, request });
              return pendingReceipt;
            },
          },
        },
      ],
    });
    const store = TestBed.inject(UpdateStore);

    const firstDecision = store.decide('APPLY');
    await store.decide('DECLINE'); // ignored while the first request is in flight

    expect(calls).toEqual([
      {
        engagementId: 'ENG-1007',
        request: { decision: 'APPLY', expectedBaselineVersion: 6, targetVersion: 8 },
      },
    ]);
    expect(store.decisionState()).toBe('SENDING');

    finish({ operationId: 'OP-7241', status: 'ACCEPTED' });
    await firstDecision;
    expect(store.decisionState()).toBe('ACCEPTED');
    expect(store.operationId()).toBe('OP-7241');
    expect(store.selected()?.status).toBe('PENDING');

    const declineStore = new UpdateStore(TestBed.inject(DecisionGateway));
    await declineStore.decide('DECLINE');
    expect(calls[1]).toEqual({
      engagementId: 'ENG-1007',
      request: { decision: 'DECLINE', expectedBaselineVersion: 6, targetVersion: 8 },
    });
  });
});
