import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ReportingApiService } from './reporting-api.service';
import { SettlementReportFilters, SettlementReportPage } from '../models/settlement-report.model';

describe('ReportingApiService', () => {
  let service: ReportingApiService;
  let httpTesting: HttpTestingController;

  const mockPage: SettlementReportPage = {
    items: [],
    page: 0,
    size: 20,
    totalElements: 0,
    totalPages: 0
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ReportingApiService, provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(ReportingApiService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should GET /api/v1/reports/settlements with mandatory page and size', () => {
    const filters: SettlementReportFilters = { page: 0, size: 20 };

    service.findSettlements(filters).subscribe(r => expect(r).toEqual(mockPage));

    const req = httpTesting.expectOne(r => r.url === '/api/v1/reports/settlements');
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('20');
    expect(req.request.params.has('from')).toBeFalse();
    req.flush(mockPage);
  });

  it('should include optional filters when provided', () => {
    const filters: SettlementReportFilters = {
      page: 1,
      size: 10,
      from: '2026-06-01',
      to: '2026-06-30',
      assignorId: 'abc-uuid',
      currency: 'BRL'
    };

    service.findSettlements(filters).subscribe();

    const req = httpTesting.expectOne(r => r.url === '/api/v1/reports/settlements');
    expect(req.request.params.get('from')).toBe('2026-06-01');
    expect(req.request.params.get('to')).toBe('2026-06-30');
    expect(req.request.params.get('assignorId')).toBe('abc-uuid');
    expect(req.request.params.get('currency')).toBe('BRL');
    req.flush(mockPage);
  });
});
