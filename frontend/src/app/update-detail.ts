import { Component, input, output } from '@angular/core';
import type { EngagementUpdate, UpdateDecision } from './update.model';

/** Review boundary: render server wording and emit Apply or Decline intent. */
@Component({
  selector: 'app-update-detail',
  template: `
    <section class="panel detail" aria-label="Selected engagement">
      <div class="detail-top">
        <div><div class="detail-label">Update details</div><h2>{{ item().name }}</h2><p class="detail-sub">{{ item().template.displayName }}</p></div>
        <span class="pill" [class.pending]="item().status === 'PENDING' && !item().declined" [class.current]="item().status === 'CURRENT'" [class.declined]="item().declined" [class.unknown]="item().status === 'UNKNOWN'">{{ item().declined ? 'Declined' : item().status === 'PENDING' ? 'Needs review' : item().status === 'CURRENT' ? 'Up to date' : 'Checking' }}</span>
      </div>
      @if (item().freshness.state === 'STALE') {
        <p class="callout warning">This information may be out of date.</p>
      }

      @if (item().status === 'PENDING') {
        <div class="version-track"><span>Current version<strong>v{{ item().baselineVersion }}</strong></span><span class="version-arrow">→</span><span>Latest template<strong>v{{ item().targetVersion }}</strong></span><span class="version-note">{{ item().pendingVersionCount }} {{ item().pendingVersionCount === 1 ? 'published update' : 'published updates' }} to review.</span></div>

        @if (item().summary; as summary) {
          @switch (summary.state) {
            @case ('AVAILABLE') {
              <div class="section-title"><h3>What changed</h3><span>From your version to the latest</span></div>
              @for (group of summary.groups; track group.section) {
                <div class="change-group"><h4>{{ group.section }}</h4>
                  @for (change of group.changes; track $index) {
                    <div class="change-row"><span class="change-dot" [class.added]="change.kind === 'ADDED'" [class.changed]="change.kind === 'CHANGED'" [class.removed]="change.kind === 'REMOVED'">{{ change.kind === 'ADDED' ? '+' : change.kind === 'REMOVED' ? '−' : '↗' }}</span><p>{{ change.description }} @if (change.reviewRecommended) { <span class="review-note">Review carefully.</span> }</p></div>
                  }
                </div>
              }
            }
            @case ('COMPUTING') {
              <p class="callout">Change summary is still being prepared.</p>
            }
            @case ('UNAVAILABLE') {
              <p class="callout warning">Change summary is unavailable right now. Please check again later.</p>
            }
          }
        } @else {
          <p class="callout">No change summary is available yet.</p>
        }

        @if (item().declined) { <p class="callout">You declined this version. Another published version will make a new review available.</p> }
        <div class="actions"><button class="action primary" type="button" [disabled]="!canDecide()" (click)="decision.emit('APPLY')">Apply update →</button><button class="action secondary" type="button" [disabled]="!canDecide()" (click)="decision.emit('DECLINE')">Decline for now</button></div>
      } @else if (item().status === 'CURRENT') {
        <p class="callout">This engagement already uses the latest template.</p>
      } @else {
        <p class="callout warning">Update information is not available yet. Please check again later.</p>
      }
    </section>
  `,
})
export class UpdateDetail {
  readonly item = input.required<EngagementUpdate>();
  readonly canDecide = input(false);
  readonly decision = output<UpdateDecision['decision']>();
}
