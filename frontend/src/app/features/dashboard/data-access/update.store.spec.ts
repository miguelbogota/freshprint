import { TestBed } from '@angular/core/testing';
import { HttpErrorResponse } from '@angular/common/http';
import { DecisionGateway } from '../../../core/services';
import type { DecisionReceipt, UpdateDecision } from './update.model';
import { UpdateStore } from './update.store';
import { updateFixture } from './update.fixture';

describe('UpdateStore', () => {
  it('keeps a recoverable error state when loading fails', async () => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: DecisionGateway,
          useValue: {
            list: async () => {
              throw new Error('offline');
            },
          },
        },
      ],
    });
    const store = TestBed.inject(UpdateStore);

    await store.load();

    expect(store.loading()).toBe(false);
    expect(store.loadError()).toBe(true);
    expect(store.items()).toEqual([]);
  });

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
            list: async () => updateFixture,
            operation: async () => new Promise(() => {}),
          },
        },
      ],
    });
    const store = TestBed.inject(UpdateStore);
    await store.load();

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
    await declineStore.load();
    await declineStore.decide('DECLINE');
    expect(calls[1]).toEqual({
      engagementId: 'ENG-1007',
      request: { decision: 'DECLINE', expectedBaselineVersion: 6, targetVersion: 8 },
    });
  });

  it('refreshes after a version conflict and asks for another review', async () => {
    let loads = 0;
    TestBed.configureTestingModule({
      providers: [
        {
          provide: DecisionGateway,
          useValue: {
            list: async () => {
              loads++;
              return updateFixture;
            },
            submit: async () => {
              throw new HttpErrorResponse({ status: 409 });
            },
          },
        },
      ],
    });
    const store = TestBed.inject(UpdateStore);
    await store.load();

    await store.decide('APPLY');

    expect(loads).toBe(2);
    expect(store.decisionState()).toBe('FAILED');
    expect(store.message()).toContain('versions changed');
  });

  it('refreshes the list after the operation succeeds', async () => {
    let loads = 0;
    TestBed.configureTestingModule({
      providers: [
        {
          provide: DecisionGateway,
          useValue: {
            list: async () => {
              loads++;
              return updateFixture;
            },
            submit: async () => ({ operationId: 'OP-1', status: 'ACCEPTED' }),
            operation: async () => ({ operationId: 'OP-1', status: 'SUCCEEDED' }),
          },
        },
      ],
    });
    const store = TestBed.inject(UpdateStore);
    await store.load();

    await store.decide('APPLY');
    await new Promise((resolve) => setTimeout(resolve, 0));

    expect(store.decisionState()).toBe('SUCCEEDED');
    expect(store.message()).toContain('Decision completed');
    expect(loads).toBe(2);
  });
});
