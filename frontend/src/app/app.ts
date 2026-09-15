import { Component, inject } from '@angular/core';
import { UpdateDetail } from './update-detail';
import { UpdateList } from './update-list';
import { UpdateStore } from './update.store';

/** Composes list, review, and action state without making HTTP requests. */
@Component({
  selector: 'app-root',
  imports: [UpdateList, UpdateDetail],
  templateUrl: './app.html',
})
export class App {
  readonly store = inject(UpdateStore);
}
