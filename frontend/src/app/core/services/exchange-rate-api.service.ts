import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RegisterExchangeRateRequest, ExchangeRateResponse } from '../models/exchange-rate.model';

@Injectable({ providedIn: 'root' })
export class ExchangeRateApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  register(request: RegisterExchangeRateRequest): Observable<ExchangeRateResponse> {
    return this.http.post<ExchangeRateResponse>(
      `${this.apiUrl}/api/v1/exchange-rates`,
      request
    );
  }

  findLatest(base: string, quote: string): Observable<ExchangeRateResponse> {
    const params = new HttpParams().set('base', base).set('quote', quote);
    return this.http.get<ExchangeRateResponse>(
      `${this.apiUrl}/api/v1/exchange-rates/latest`,
      { params }
    );
  }
}
