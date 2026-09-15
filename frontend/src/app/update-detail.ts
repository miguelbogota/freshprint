import { Component, input, output } from '@angular/core';
import type { EngagementUpdate, UpdateDecision } from './update.model';

/** Review boundary: render server wording and emit Apply or Decline intent. */
@Component({
  selector: 'app-update-detail',
  template: `
    <section aria-label="Selected engagement">
      <h2>{{ item().name }}</h2>
      <p>{{ item().template.displayName }} — {{ item().status }}</p>
      <p>Freshness: {{ item().freshness.state }} (checked {{ item().freshness.checkedAt }})</p>

      @if (item().status === 'PENDING') {
        <p>
          Template version {{ item().baselineVersion }} → {{ item().targetVersion }}.
          {{ item().pendingVersionCount }} published update(s) to review.
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
              <p>Change summary is unavailable: {{ summary.reason }}.</p>
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
