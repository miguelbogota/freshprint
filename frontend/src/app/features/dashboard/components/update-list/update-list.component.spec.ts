import { TestBed } from '@angular/core/testing';
import { updateFixture } from '../../data-access/update.fixture';
import { UpdateListComponent } from './update-list.component';

describe('UpdateListComponent', () => {
  it('renders states, selection, and emits user intent', async () => {
    await TestBed.configureTestingModule({ imports: [UpdateListComponent] }).compileComponents();
    const fixture = TestBed.createComponent(UpdateListComponent);
    fixture.componentRef.setInput('items', updateFixture);
    fixture.componentRef.setInput('total', updateFixture.length);
    fixture.componentRef.setInput('selectedId', 'ENG-1007');
    const selected: string[] = [];
    const filters: string[] = [];
    const queries: string[] = [];
    fixture.componentInstance.selected.subscribe((id) => selected.push(id));
    fixture.componentInstance.filterChange.subscribe((filter) => filters.push(filter));
    fixture.componentInstance.queryChange.subscribe((query) => queries.push(query));
    fixture.detectChanges();

    const page = fixture.nativeElement as HTMLElement;
    expect(page.textContent).toContain('Update to review (2 newer versions)');
    expect(page.textContent).toContain('Up to date');
    expect(page.querySelector('.engagement.selected')?.getAttribute('aria-current')).toBe('true');

    (page.querySelectorAll('.engagement')[1] as HTMLButtonElement).click();
    (page.querySelectorAll('.filter')[1] as HTMLButtonElement).click();
    const search = page.querySelector('.search') as HTMLInputElement;
    search.value = 'Bluewater';
    search.dispatchEvent(new Event('input'));

    expect(selected).toEqual(['ENG-1003']);
    expect(filters).toEqual(['PENDING']);
    expect(queries).toEqual(['Bluewater']);
  });

  it('shows an empty state for no matching engagements', async () => {
    await TestBed.configureTestingModule({ imports: [UpdateListComponent] }).compileComponents();
    const fixture = TestBed.createComponent(UpdateListComponent);
    fixture.componentRef.setInput('items', []);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain(
      'No engagements match this search.',
    );
  });
});
