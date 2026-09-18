import type { Routes } from '@angular/router';

export const appRoutes: Routes = [
  {
    path: '',
    loadChildren: () =>
      import('./features/dashboard/dashboard.routes').then((routes) => routes.dashboardRoutes),
  },
  { path: '**', redirectTo: '' },
];
