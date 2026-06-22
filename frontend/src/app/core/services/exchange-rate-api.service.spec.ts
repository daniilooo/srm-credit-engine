import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ExchangeRateApiService } from './exchange-rate-api.service';
import { RegisterExchangeRateRequest, ExchangeRateResponse } from '../models/exchange-rate.model';

describe('ExchangeRateApiService', () => {
  let service: ExchangeRateApiService;
  let httpTesting: HttpTestingController;

  const mockResponse: ExchangeRateResponse = {
    baseCurrencyCode: 'BRL',
    quoteCurrencyCode: 'USD',
    rateValue: 0.18,
    validFrom: '2026-06-21T00:00:00Z',
    capturedAt: '2026-06-21T10:00:00Z'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ExchangeRateApiService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(ExchangeRateApiService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should POST to /api/v1/exchange-rates', () => {
    const request: RegisterExchangeRateRequest = {
      baseCurrency: 'BRL',
      quoteCurrency: 'USD',
      rateValue: 0.18,
      validFrom: '2026-06-21T00:00:00.000Z'
    };

    service.register(request).subscribe(r => expect(r).toEqual(mockResponse));

    const req = httpTesting.expectOne('/api/v1/exchange-rates');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should GET /api/v1/exchange-rates/latest with base and quote params', () => {
    service.findLatest('BRL', 'USD').subscribe(r => expect(r).toEqual(mockResponse));

    const req = httpTesting.expectOne(r => r.url === '/api/v1/exchange-rates/latest');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('base')).toBe('BRL');
    expect(req.request.params.get('quote')).toBe('USD');
    req.flush(mockResponse);
  });
});
