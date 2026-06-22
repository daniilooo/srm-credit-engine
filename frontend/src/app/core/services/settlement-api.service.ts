import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SettleReceivableRequest, SettleReceivableResponse } from '../models/settlement.model';

@Injectable({ providedIn: 'root' })
export class SettlementApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  settle(request: SettleReceivableRequest): Observable<SettleReceivableResponse> {
    return this.http.post<SettleReceivableResponse>(
      `${this.apiUrl}/api/v1/settlements`,
      request
    );
  }
}
