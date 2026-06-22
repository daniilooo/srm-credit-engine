import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';
import { SettlementApiService } from '../../../core/services/settlement-api.service';
import { SettleReceivableRequest, SettleReceivableResponse } from '../../../core/models/settlement.model';
import { CurrencyCode } from '../../../core/models/exchange-rate.model';
import { ApiError } from '../../../core/models/api-error.model';

const UUID_PATTERN = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

@Component({
  selector: 'app-settlement-form',
  imports: [ReactiveFormsModule],
  templateUrl: './settlement-form.component.html',
  styleUrl: './settlement-form.component.scss'
})
export class SettlementFormComponent {
  private readonly settlementService = inject(SettlementApiService);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(false);
  readonly result = signal<SettleReceivableResponse | null>(null);
  readonly errorMessage = signal<string | null>(null);

  readonly form = this.fb.group({
    receivableId: ['', [Validators.required, Validators.pattern(UUID_PATTERN)]],
    paymentCurrencyCode: ['BRL' as CurrencyCode, Validators.required],
    baseTaxMonthly: [null as number | null, [Validators.required, Validators.min(0.000001)]]
  });

  settle(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.errorMessage.set(null);
    this.result.set(null);

    const v = this.form.getRawValue();
    const request: SettleReceivableRequest = {
      receivableId: v.receivableId!,
      paymentCurrencyCode: v.paymentCurrencyCode!,
      baseTaxMonthly: v.baseTaxMonthly!
    };

    this.settlementService.settle(request)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: r => this.result.set(r),
        error: (e: HttpErrorResponse) => {
          const apiError = e.error as ApiError;
          this.errorMessage.set(apiError?.message ?? 'Erro ao liquidar recebível.');
        }
      });
  }
}
