import { appRoutes } from './app.routes';

describe('appRoutes', () => {
  it('loads the dashboard feature lazily and redirects unknown paths', async () => {
    const feature = await appRoutes[0].loadChildren?.();

    expect(Array.isArray(feature)).toBe(true);
    expect(appRoutes[0].path).toBe('');
    expect(appRoutes[1]).toMatchObject({ path: '**', redirectTo: '' });
  });
});
