import { TestBed } from '@angular/core/testing';
import { App } from './app';

describe('App', () => {
  it('shows readable pending changes and blocks decisions while a summary computes', async () => {
    await TestBed.configureTestingModule({ imports: [App] }).compileComponents();
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    fixture.detectChanges();
    const page = fixture.nativeElement as HTMLElement;

    expect(page.textContent).toContain('Tolerance changed from 0.15 to 0.1.');
    expect(page.textContent).toContain('2 published update(s) to review.');
    expect(page.querySelectorAll('app-update-detail button:disabled')).toHaveLength(0);

    const computingItem = Array.from(page.querySelectorAll('app-update-list button')).find(
      (button) => button.textContent?.includes('Harbourview Logistics'),
    );
    expect(computingItem).toBeTruthy();
    (computingItem as HTMLButtonElement).click();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(page.textContent).toContain('Change summary is still being prepared.');
    expect(page.querySelectorAll('app-update-detail button:disabled')).toHaveLength(2);
  });
});
