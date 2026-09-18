import { Component, input, output } from '@angular/core';
import type { EngagementUpdate } from '../../data-access';

/** Searchable engagement list with clear update states. */
@Component({
  selector: 'app-update-list',
  styleUrl: './update-list.component.scss',
  template: `
    <section class="panel" aria-label="Engagement updates">
      <div class="panel-top">
        <h2>Engagements</h2>
        <small>{{ total() }} total</small>
      </div>
      <div class="search-wrap">
        <input
          class="search"
          type="search"
          aria-label="Search engagements"
          placeholder="Search engagements…"
          [value]="query()"
          (input)="queryChange.emit($any($event.target).value)"
        />
      </div>
      <div class="filters" aria-label="Filter engagements">
        <button
          type="button"
          class="filter"
          [class.active]="filter() === 'ALL'"
          (click)="filterChange.emit('ALL')"
        >
          All
        </button>
        <button
          type="button"
          class="filter"
          [class.active]="filter() === 'PENDING'"
          (click)="filterChange.emit('PENDING')"
        >
          Needs review
        </button>
        <button
          type="button"
          class="filter"
          [class.active]="filter() === 'CURRENT'"
          (click)="filterChange.emit('CURRENT')"
        >
          Up to date
        </button>
      </div>
      <div class="list-scroll">
        @for (item of items(); track item.engagementId) {
          <button
            class="engagement"
            type="button"
            [class.selected]="selectedId() === item.engagementId"
            [attr.aria-current]="selectedId() === item.engagementId ? 'true' : null"
            (click)="selected.emit(item.engagementId)"
          >
            <span class="file-icon" aria-hidden="true">▤</span>
            <span class="engagement-body">
              <span class="engagement-name">{{ item.name }}</span>
              <span class="engagement-template">{{ item.template.displayName }}</span>
              @if (item.declined) {
                <span class="pill declined">Declined for now</span>
              } @else if (item.status === 'PENDING') {
                <span class="pill pending"
                  >Update to review ({{ item.pendingVersionCount }}
                  {{ item.pendingVersionCount === 1 ? 'newer version' : 'newer versions' }})</span
                >
              } @else if (item.status === 'CURRENT') {
                <span class="pill current">Up to date</span>
              } @else {
                <span class="pill unknown">Checking update status</span>
              }
            </span>
          </button>
        } @empty {
          <div class="empty-list">No engagements match this search.</div>
        }
      </div>
    </section>
  `,
})
export class UpdateListComponent {
  readonly items = input.required<readonly EngagementUpdate[]>();
  readonly total = input(0);
  readonly selectedId = input<string | null>(null);
  readonly query = input('');
  readonly filter = input<'ALL' | 'PENDING' | 'CURRENT'>('ALL');
  readonly selected = output<string>();
  readonly queryChange = output<string>();
  readonly filterChange = output<'ALL' | 'PENDING' | 'CURRENT'>();
}
