import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { finalize } from 'rxjs';
import { ReportingApiService } from '../../../core/services/reporting-api.service';
import { SettlementReportFilters, SettlementReportPage } from '../../../core/models/settlement-report.model';
import { CurrencyCode } from '../../../core/models/exchange-rate.model';
import { ApiError } from '../../../core/models/api-error.model';

@Component({
  selector: 'app-settlement-report',
  imports: [ReactiveFormsModule],
  templateUrl: './settlement-report.component.html',
  styleUrl: './settlement-report.component.scss'
})
export class SettlementReportComponent implements OnInit {
  private readonly reportingService = inject(ReportingApiService);
  private readonly fb = inject(FormBuilder);

  readonly loading = signal(false);
  readonly reportPage = signal<SettlementReportPage | null>(null);
  readonly errorMessage = signal<string | null>(null);
  readonly currentPage = signal(0);

  readonly filtersForm = this.fb.group({
    from: [''],
    to: [''],
    assignorId: [''],
    currency: ['' as CurrencyCode | ''],
    size: [20]
  });

  ngOnInit(): void {
    this.search(true);
  }

  search(resetPage = true): void {
    if (resetPage) this.currentPage.set(0);
    this.load();
  }

  goToPage(page: number): void {
    this.currentPage.set(page);
    this.load();
  }

  get totalPages(): number {
    return this.reportPage()?.totalPages ?? 0;
  }

  get hasNext(): boolean {
    return this.currentPage() < this.totalPages - 1;
  }

  get hasPrev(): boolean {
    return this.currentPage() > 0;
  }

  private load(): void {
    this.loading.set(true);
    this.errorMessage.set(null);

    const f = this.filtersForm.getRawValue();
    const filters: SettlementReportFilters = {
      page: this.currentPage(),
      size: f.size ?? 20
    };
    if (f.from) filters.from = f.from;
    if (f.to) filters.to = f.to;
    if (f.assignorId) filters.assignorId = f.assignorId;
    if (f.currency) filters.currency = f.currency as CurrencyCode;

    this.reportingService.findSettlements(filters)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: page => this.reportPage.set(page),
        error: (e: HttpErrorResponse) => {
          const apiError = e.error as ApiError;
          this.errorMessage.set(apiError?.message ?? 'Erro ao carregar extrato.');
        }
      });
  }
}
