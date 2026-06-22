import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { PricingApiService } from './pricing-api.service';
import { PricingSimulationRequest, PricingSimulationResponse } from '../models/pricing.model';

describe('PricingApiService', () => {
  let service: PricingApiService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PricingApiService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(PricingApiService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should POST to /api/v1/pricing/simulations', () => {
    const request: PricingSimulationRequest = {
      faceValue: 10000,
      dueDate: '2026-12-31',
      receivableType: 'DUPLICATA',
      baseTaxMonthly: 0.02
    };
    const mockResponse: PricingSimulationResponse = {
      presentValue: 9500,
      appliedTax: 0.02,
      appliedSpread: 0.005,
      termInMonths: 6
    };

    service.simulate(request).subscribe(r => expect(r).toEqual(mockResponse));

    const req = httpTesting.expectOne('/api/v1/pricing/simulations');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(mockResponse);
  });
});
