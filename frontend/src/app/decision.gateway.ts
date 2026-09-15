import { Injectable } from '@angular/core';
import type { DecisionReceipt, UpdateDecision } from './update.model';

/** In-memory stand-in for the documented decision endpoint. */
@Injectable({ providedIn: 'root' })
export class DecisionGateway {
  submit(_engagementId: string, _decision: UpdateDecision): Promise<DecisionReceipt> {
    // Keep the request shape visible, but do not merge template content here.
    return Promise.resolve({ operationId: 'OP-DEMO', status: 'ACCEPTED' });
  }
}
