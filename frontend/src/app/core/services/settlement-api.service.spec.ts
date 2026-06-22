import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { SettlementApiService } from './settlement-api.service';
import { SettleReceivableRequest, SettleReceivableResponse } from '../models/settlement.model';

describe('SettlementApiService', () => {
  let service: SettlementApiService;
  let httpTesting: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SettlementApiService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(SettlementApiService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should POST to /api/v1/settlements', () => {
    const request: SettleReceivableRequest = {
      receivableId: 'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
      paymentCurrencyCode: 'BRL',
      baseTaxMonthly: 0.025
    };
    const mockResponse: SettleReceivableResponse = {
      settlementId: 'f1e2d3c4-b5a6-7890-fedc-ba0987654321'
    };

    service.settle(request).subscribe(r => expect(r).toEqual(mockResponse));

    const req = httpTesting.expectOne('/api/v1/settlements');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(mockResponse, { status: 201, statusText: 'Created' });
  });
});
