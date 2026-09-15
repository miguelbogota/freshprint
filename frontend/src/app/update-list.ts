import { Component, input, output } from '@angular/core';
import type { EngagementUpdate } from './update.model';

/** Small list boundary: show states and emit the selected engagement ID. */
@Component({
  selector: 'app-update-list',
  template: `
    <section aria-label="Engagement updates">
      <h2>Engagements</h2>
      <ul>
        @for (item of items(); track item.engagementId) {
          <li>
            <button
              type="button"
              (click)="selected.emit(item.engagementId)"
              [attr.aria-current]="selectedId() === item.engagementId ? 'true' : null"
            >
              {{ item.name }} —
              @switch (item.status) {
                @case ('PENDING') {
                  Update to review ({{ item.pendingVersionCount }}
                  {{ item.pendingVersionCount === 1 ? 'newer version' : 'newer versions' }})
                }
                @case ('CURRENT') {
                  Up to date
                }
                @case ('UNKNOWN') {
                  Checking update status
                }
              }
            </button>
          </li>
        }
      </ul>
    </section>
  `,
})
export class UpdateList {
  readonly items = input.required<readonly EngagementUpdate[]>();
  readonly selectedId = input<string | null>(null);
  readonly selected = output<string>();
}
