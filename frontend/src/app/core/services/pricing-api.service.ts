import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PricingSimulationRequest, PricingSimulationResponse } from '../models/pricing.model';

@Injectable({ providedIn: 'root' })
export class PricingApiService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  simulate(request: PricingSimulationRequest): Observable<PricingSimulationResponse> {
    return this.http.post<PricingSimulationResponse>(
      `${this.apiUrl}/api/v1/pricing/simulations`,
      request
    );
  }
}
