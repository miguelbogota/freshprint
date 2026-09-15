import { Component, input, output } from '@angular/core';
import type { EngagementUpdate, UpdateDecision } from './update.model';

/** Review boundary: render server wording and emit Apply or Decline intent. */
@Component({
  selector: 'app-update-detail',
  template: `
    <section aria-label="Selected engagement">
      <h2>{{ item().name }}</h2>
      <p>{{ item().template.displayName }}</p>
      <p>Last checked: {{ item().freshness.checkedAt }}.</p>
      @if (item().freshness.state === 'STALE') {
        <p>This information may be out of date.</p>
      }

      @if (item().status === 'PENDING') {
        <p>
          Template version {{ item().baselineVersion }} → {{ item().targetVersion }}.
          {{ item().pendingVersionCount }}
          {{ item().pendingVersionCount === 1 ? 'published update' : 'published updates' }} to
          review.
        </p>

        @if (item().summary; as summary) {
          @switch (summary.state) {
            @case ('AVAILABLE') {
              <h3>What changed</h3>
              @for (group of summary.groups; track group.section) {
                <h4>{{ group.section }}</h4>
                <ul>
                  @for (change of group.changes; track $index) {
                    <li>
                      {{ change.description }}
                      @if (change.reviewRecommended) {
                        Review this change carefully.
                      }
                    </li>
                  }
                </ul>
              }
            }
            @case ('COMPUTING') {
              <p>Change summary is still being prepared.</p>
            }
            @case ('UNAVAILABLE') {
              <p>Change summary is unavailable right now. Please check again later.</p>
            }
          }
        } @else {
          <p>No change summary is available yet.</p>
        }

        <button type="button" [disabled]="!canDecide()" (click)="decision.emit('APPLY')">
          Apply
        </button>
        <button type="button" [disabled]="!canDecide()" (click)="decision.emit('DECLINE')">
          Decline
        </button>
      } @else if (item().status === 'CURRENT') {
        <p>This engagement already uses the latest template.</p>
      } @else {
        <p>Update information is not available yet. Please check again later.</p>
      }
    </section>
  `,
})
export class UpdateDetail {
  readonly item = input.required<EngagementUpdate>();
  readonly canDecide = input(false);
  readonly decision = output<UpdateDecision['decision']>();
}
