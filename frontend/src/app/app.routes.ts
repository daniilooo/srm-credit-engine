import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'pricing', pathMatch: 'full' },
  {
    path: 'pricing',
    loadComponent: () => import('./features/pricing/pricing-simulation/pricing-simulation.component')
      .then(m => m.PricingSimulationComponent)
  },
  {
    path: 'exchange-rates',
    loadComponent: () => import('./features/exchange-rates/exchange-rates/exchange-rates.component')
      .then(m => m.ExchangeRatesComponent)
  },
  {
    path: 'settlements',
    loadComponent: () => import('./features/settlement/settlement-form/settlement-form.component')
      .then(m => m.SettlementFormComponent)
  },
  {
    path: 'reports/settlements',
    loadComponent: () => import('./features/reporting/settlement-report/settlement-report.component')
      .then(m => m.SettlementReportComponent)
  }
];
