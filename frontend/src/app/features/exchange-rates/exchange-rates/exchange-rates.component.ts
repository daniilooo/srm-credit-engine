import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';
import { ExchangeRateApiService } from '../../../core/services/exchange-rate-api.service';
import { RegisterExchangeRateRequest, ExchangeRateResponse, CurrencyCode } from '../../../core/models/exchange-rate.model';
import { ApiError } from '../../../core/models/api-error.model';

@Component({
  selector: 'app-exchange-rates',
  imports: [ReactiveFormsModule],
  templateUrl: './exchange-rates.component.html',
  styleUrl: './exchange-rates.component.scss'
})
export class ExchangeRatesComponent {
  private readonly exchangeRateService = inject(ExchangeRateApiService);
  private readonly fb = inject(FormBuilder);

  readonly registerLoading = signal(false);
  readonly registerResult = signal<ExchangeRateResponse | null>(null);
  readonly registerError = signal<string | null>(null);

  readonly findLoading = signal(false);
  readonly findResult = signal<ExchangeRateResponse | null>(null);
  readonly findError = signal<string | null>(null);

  readonly registerForm = this.fb.group({
    baseCurrency: ['BRL' as CurrencyCode, Validators.required],
    quoteCurrency: ['USD' as CurrencyCode, Validators.required],
    rateValue: [null as number | null, [Validators.required, Validators.min(0.000001)]],
    validFrom: ['', Validators.required]
  });

  readonly findForm = this.fb.group({
    base: ['BRL' as CurrencyCode, Validators.required],
    quote: ['USD' as CurrencyCode, Validators.required]
  });

  register(): void {
    if (this.registerForm.invalid) return;
    this.registerLoading.set(true);
    this.registerError.set(null);
    this.registerResult.set(null);

    const v = this.registerForm.getRawValue();
    const request: RegisterExchangeRateRequest = {
      baseCurrency: v.baseCurrency!,
      quoteCurrency: v.quoteCurrency!,
      rateValue: v.rateValue!,
      validFrom: new Date(v.validFrom!).toISOString()
    };

    this.exchangeRateService.register(request)
      .pipe(finalize(() => this.registerLoading.set(false)))
      .subscribe({
        next: r => this.registerResult.set(r),
        error: (e: HttpErrorResponse) => {
          const apiError = e.error as ApiError;
          this.registerError.set(apiError?.message ?? 'Erro ao registrar taxa.');
        }
      });
  }

  findLatest(): void {
    if (this.findForm.invalid) return;
    this.findLoading.set(true);
    this.findError.set(null);
    this.findResult.set(null);

    const v = this.findForm.getRawValue();

    this.exchangeRateService.findLatest(v.base!, v.quote!)
      .pipe(finalize(() => this.findLoading.set(false)))
      .subscribe({
        next: r => this.findResult.set(r),
        error: (e: HttpErrorResponse) => {
          const apiError = e.error as ApiError;
          this.findError.set(apiError?.message ?? 'Taxa não encontrada.');
        }
      });
  }
}
