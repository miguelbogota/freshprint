import { DashboardComponent } from './dashboard.component';
import { dashboardRoutes } from './dashboard.routes';

describe('dashboardRoutes', () => {
  it('loads the dashboard component lazily', async () => {
    const component = await dashboardRoutes[0].loadComponent?.();

    expect(component).toBe(DashboardComponent);
  });
});
