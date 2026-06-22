import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SettlementReportFilters, SettlementReportPage } from '../models/settlement-report.model';

@Injectable({ providedIn: 'root' })
export class ReportingApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  findSettlements(filters: SettlementReportFilters): Observable<SettlementReportPage> {
    let params = new HttpParams()
      .set('page', filters.page.toString())
      .set('size', filters.size.toString());

    if (filters.from) params = params.set('from', filters.from);
    if (filters.to) params = params.set('to', filters.to);
    if (filters.assignorId) params = params.set('assignorId', filters.assignorId);
    if (filters.currency) params = params.set('currency', filters.currency);

    return this.http.get<SettlementReportPage>(
      `${this.apiUrl}/api/v1/reports/settlements`,
      { params }
    );
  }
}
