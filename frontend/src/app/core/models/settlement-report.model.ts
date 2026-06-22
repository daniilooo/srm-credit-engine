import { CurrencyCode } from './exchange-rate.model';

export interface SettlementReportItem {
  settlementId: string;
  receivableId: string;
  assignorId: string;
  assignorName: string;
  receivableType: string;
  faceValue: number;
  settledAmount: number;
  receivableCurrency: string;
  paymentCurrency: string;
  exchangeRateValue: number;
  exchangeRateBaseCurrency: string;
  exchangeRateQuoteCurrency: string;
  exchangeRateUsedAt: string;
  settlementStatus: string;
  settledAt: string;
  dueDate: string;
}

export interface SettlementReportPage {
  items: SettlementReportItem[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface SettlementReportFilters {
  from?: string;
  to?: string;
  assignorId?: string;
  currency?: CurrencyCode;
  page: number;
  size: number;
}
