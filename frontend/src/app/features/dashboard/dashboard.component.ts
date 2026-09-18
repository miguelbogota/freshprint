import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { UpdateDetailComponent, UpdateListComponent } from './components';
import { UpdateStore } from './data-access';

/** Live update dashboard backed by the Spring Boot API. */
@Component({
  selector: 'app-dashboard',
  imports: [UpdateListComponent, UpdateDetailComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent implements OnInit {
  readonly store = inject(UpdateStore);
  readonly query = signal('');
  readonly filter = signal<'ALL' | 'PENDING' | 'CURRENT'>('ALL');
  readonly visibleItems = computed(() =>
    this.store.items().filter((item) => {
      const matchesText = `${item.name} ${item.template.displayName}`
        .toLowerCase()
        .includes(this.query().toLowerCase());
      return matchesText && (this.filter() === 'ALL' || item.status === this.filter());
    }),
  );
  readonly pendingCount = computed(
    () => this.store.items().filter((item) => item.status === 'PENDING' && !item.declined).length,
  );
  readonly currentCount = computed(
    () => this.store.items().filter((item) => item.status === 'CURRENT').length,
  );

  ngOnInit(): void {
    void this.store.load();
  }
}
