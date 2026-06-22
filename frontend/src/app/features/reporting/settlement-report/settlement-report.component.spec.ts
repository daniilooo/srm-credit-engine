import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { SettlementReportComponent } from './settlement-report.component';

describe('SettlementReportComponent', () => {
  let httpTesting: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettlementReportComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpTesting.verify());

  it('should create', () => {
    const fixture = TestBed.createComponent(SettlementReportComponent);
    fixture.detectChanges();
    // flush the ngOnInit auto-search
    const req = httpTesting.expectOne(r => r.url === '/api/v1/reports/settlements');
    req.flush({ items: [], page: 0, size: 20, totalElements: 0, totalPages: 0 });
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should auto-search on init with page 0', () => {
    const fixture = TestBed.createComponent(SettlementReportComponent);
    fixture.detectChanges();
    const req = httpTesting.expectOne(r => r.url === '/api/v1/reports/settlements');
    expect(req.request.params.get('page')).toBe('0');
    req.flush({ items: [], page: 0, size: 20, totalElements: 0, totalPages: 0 });
  });

  it('should report hasPrev false and hasNext false on single page result', () => {
    const fixture = TestBed.createComponent(SettlementReportComponent);
    fixture.detectChanges();
    const req = httpTesting.expectOne(r => r.url === '/api/v1/reports/settlements');
    req.flush({ items: [], page: 0, size: 20, totalElements: 0, totalPages: 1 });
    expect(fixture.componentInstance.hasPrev).toBeFalse();
    expect(fixture.componentInstance.hasNext).toBeFalse();
  });
});
