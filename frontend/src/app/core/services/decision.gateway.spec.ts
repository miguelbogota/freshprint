import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { DecisionGateway } from './decision.gateway';
import { updateFixture } from '../../features/dashboard/data-access/update.fixture';

describe('DecisionGateway', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
  });

  afterEach(() => TestBed.inject(HttpTestingController).verify());

  it('reads the engagement list from the API', async () => {
    const gateway = TestBed.inject(DecisionGateway);
    const result = gateway.list();
    TestBed.inject(HttpTestingController)
      .expectOne('/api/engagements/template-updates')
      .flush({ items: updateFixture });
    expect(await result).toEqual(updateFixture);
  });

  it('sends reviewed versions and encodes resource identifiers', async () => {
    const gateway = TestBed.inject(DecisionGateway);
    const decision = { decision: 'APPLY' as const, expectedBaselineVersion: 6, targetVersion: 8 };
    const result = gateway.submit('ENG/1007', decision);
    const request = TestBed.inject(HttpTestingController).expectOne(
      '/api/engagements/ENG%2F1007/template-update-decisions',
    );
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(decision);
    request.flush({ operationId: 'OP-1', status: 'ACCEPTED' });
    expect((await result).operationId).toBe('OP-1');
  });

  it('reads the final operation result', async () => {
    const result = TestBed.inject(DecisionGateway).operation('OP/1');
    const request = TestBed.inject(HttpTestingController).expectOne(
      '/api/template-update-operations/OP%2F1',
    );
    expect(request.request.method).toBe('GET');
    request.flush({ operationId: 'OP/1', status: 'SUCCEEDED' });
    expect((await result).status).toBe('SUCCEEDED');
  });
});
