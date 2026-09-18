import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import type {
  DecisionOperation,
  DecisionReceipt,
  EngagementUpdate,
  UpdateDecision,
} from '../../features/dashboard/data-access';

/** Small HTTP boundary for the Spring Boot API. */
@Injectable({ providedIn: 'root' })
export class DecisionGateway {
  private readonly http = inject(HttpClient);

  async list(): Promise<EngagementUpdate[]> {
    const response = await firstValueFrom(
      this.http.get<{ items: EngagementUpdate[] }>('/api/engagements/template-updates'),
    );
    return response.items;
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
      this.http.get<DecisionOperation>(
        `/api/template-update-operations/${encodeURIComponent(operationId)}`,
      ),
    );
  }
}
