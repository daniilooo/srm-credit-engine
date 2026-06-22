import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';
import { PricingApiService } from '../../../core/services/pricing-api.service';
import { PricingSimulationRequest, PricingSimulationResponse } from '../../../core/models/pricing.model';
import { ApiError } from '../../../core/models/api-error.model';

@Component({
  selector: 'app-pricing-simulation',
  imports: [ReactiveFormsModule],
  templateUrl: './pricing-simulation.component.html',
  styleUrl: './pricing-simulation.component.scss'
})
export class PricingSimulationComponent {
  private readonly pricingService = inject(PricingApiService);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(false);
  readonly result = signal<PricingSimulationResponse | null>(null);
  readonly errorMessage = signal<string | null>(null);

  readonly form = this.fb.group({
    faceValue: [null as number | null, [Validators.required, Validators.min(0.01)]],
    dueDate: ['', Validators.required],
    receivableType: ['DUPLICATA', Validators.required],
    baseTaxMonthly: [null as number | null, [Validators.required, Validators.min(0.000001)]]
  });

  simulate(): void {
    if (this.form.invalid) return;
    this.loading.set(true);
    this.errorMessage.set(null);
    this.result.set(null);

    const v = this.form.getRawValue();
    const request: PricingSimulationRequest = {
      faceValue: v.faceValue!,
      dueDate: v.dueDate!,
      receivableType: v.receivableType!,
      baseTaxMonthly: v.baseTaxMonthly!
    };

    this.pricingService.simulate(request)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: r => this.result.set(r),
        error: (e: HttpErrorResponse) => {
          const apiError = e.error as ApiError;
          this.errorMessage.set(apiError?.message ?? 'Erro inesperado. Tente novamente.');
        }
      });
  }
}
