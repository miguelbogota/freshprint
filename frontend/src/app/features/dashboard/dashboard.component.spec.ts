import { TestBed } from '@angular/core/testing';
import { DashboardComponent } from './dashboard.component';
import { DecisionGateway } from '../../core/services';
import { updateFixture } from './data-access/update.fixture';

describe('DashboardComponent', () => {
  it('shows readable pending changes and blocks decisions while a summary computes', async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [{ provide: DecisionGateway, useValue: { list: async () => updateFixture } }],
    }).compileComponents();
    const fixture = TestBed.createComponent(DashboardComponent);
    await fixture.whenStable();
    fixture.detectChanges();
    const page = fixture.nativeElement as HTMLElement;

    expect(page.textContent).toContain('Tolerance changed from 0.15 to 0.1.');
    expect(page.textContent).toContain('2 published updates to review.');
    expect(page.textContent).toContain('Update to review (2 newer versions)');
    expect(page.querySelectorAll('app-update-detail button:disabled')).toHaveLength(0);

    const computingItem = Array.from(page.querySelectorAll('app-update-list .engagement')).find(
      (button) => button.textContent?.includes('Harbourview Logistics'),
    );
    expect(computingItem).toBeTruthy();
    (computingItem as HTMLButtonElement).click();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(page.textContent).toContain('Change summary is still being prepared.');
    expect(page.querySelectorAll('app-update-detail button:disabled')).toHaveLength(2);

    const unknownItem = Array.from(page.querySelectorAll('app-update-list .engagement')).find(
      (button) => button.textContent?.includes('New engagement awaiting metadata'),
    );
    expect(unknownItem).toBeTruthy();
    (unknownItem as HTMLButtonElement).click();
    await fixture.whenStable();
    fixture.detectChanges();
    expect(page.textContent).toContain('This information may be out of date.');
    expect(page.querySelectorAll('app-update-detail button')).toHaveLength(0);
  });
});
