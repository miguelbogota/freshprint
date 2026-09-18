import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { UpdateDetail } from './update-detail';
import { UpdateList } from './update-list';
import { UpdateStore } from './update.store';

/** Live update dashboard backed by the Spring Boot API. */
@Component({
  selector: 'app-root',
  imports: [UpdateList, UpdateDetail],
  templateUrl: './app.html',
})
export class App implements OnInit {
  readonly store = inject(UpdateStore);
  readonly query = signal('');
  readonly filter = signal<'ALL' | 'PENDING' | 'CURRENT'>('ALL');
  readonly visibleItems = computed(() => this.store.items().filter((item) => {
    const matchesText = `${item.name} ${item.template.displayName}`.toLowerCase().includes(this.query().toLowerCase());
    return matchesText && (this.filter() === 'ALL' || item.status === this.filter());
  }));
  readonly pendingCount = computed(() => this.store.items().filter((item) => item.status === 'PENDING' && !item.declined).length);
  readonly currentCount = computed(() => this.store.items().filter((item) => item.status === 'CURRENT').length);

  ngOnInit(): void {
    void this.store.load();
  }
}
