import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import type { DecisionOperation, DecisionReceipt, EngagementUpdate, UpdateDecision } from './update.model';

/** Small HTTP boundary for the Spring Boot API. */
@Injectable({ providedIn: 'root' })
export class DecisionGateway {
  private readonly http = inject(HttpClient);

  list(): Promise<EngagementUpdate[]> {
    return firstValueFrom(
      this.http.get<{ items: EngagementUpdate[] }>('/api/engagements/template-updates'),
    ).then((response) => response.items);
  }

  submit(engagementId: string, decision: UpdateDecision): Promise<DecisionReceipt> {
    return firstValueFrom(
      this.http.post<DecisionReceipt>(
        `/api/engagements/${encodeURIComponent(engagementId)}/template-update-decisions`,
        decision,
      ),
    );
  }

  operation(operationId: string): Promise<DecisionOperation> {
    return firstValueFrom(
      this.http.get<DecisionOperation>(`/api/template-update-operations/${encodeURIComponent(operationId)}`),
    );
  }
}
