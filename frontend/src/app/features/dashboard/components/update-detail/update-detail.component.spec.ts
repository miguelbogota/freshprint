import { TestBed } from '@angular/core/testing';
import { updateFixture } from '../../data-access/update.fixture';
import { UpdateDetailComponent } from './update-detail.component';

describe('UpdateDetailComponent', () => {
  it('shows the effective summary and emits enabled decisions', async () => {
    await TestBed.configureTestingModule({ imports: [UpdateDetailComponent] }).compileComponents();
    const fixture = TestBed.createComponent(UpdateDetailComponent);
    fixture.componentRef.setInput('item', updateFixture[0]);
    fixture.componentRef.setInput('canDecide', true);
    const decisions: string[] = [];
    fixture.componentInstance.decision.subscribe((decision) => decisions.push(decision));
    fixture.detectChanges();

    const page = fixture.nativeElement as HTMLElement;
    expect(page.textContent).toContain('Tolerance changed from 0.15 to 0.1.');
    expect(page.textContent).toContain('2 published updates to review.');
    const buttons = page.querySelectorAll('.actions button');
    (buttons[0] as HTMLButtonElement).click();
    (buttons[1] as HTMLButtonElement).click();
    expect(decisions).toEqual(['APPLY', 'DECLINE']);
  });

  it('disables decisions when a summary is still computing', async () => {
    await TestBed.configureTestingModule({ imports: [UpdateDetailComponent] }).compileComponents();
    const fixture = TestBed.createComponent(UpdateDetailComponent);
    fixture.componentRef.setInput('item', updateFixture[1]);
    fixture.detectChanges();
    const page = fixture.nativeElement as HTMLElement;
    expect(page.textContent).toContain('Change summary is still being prepared.');
    expect(page.querySelectorAll('.actions button:disabled')).toHaveLength(2);
  });

  it('explains stale unknown metadata without offering a decision', async () => {
    await TestBed.configureTestingModule({ imports: [UpdateDetailComponent] }).compileComponents();
    const fixture = TestBed.createComponent(UpdateDetailComponent);
    fixture.componentRef.setInput('item', updateFixture[3]);
    fixture.detectChanges();
    const page = fixture.nativeElement as HTMLElement;
    expect(page.textContent).toContain('This information may be out of date.');
    expect(page.querySelectorAll('.actions button')).toHaveLength(0);
  });
});
