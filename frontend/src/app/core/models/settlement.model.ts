import { CurrencyCode } from './exchange-rate.model';

export interface SettleReceivableRequest {
  receivableId: string;
  paymentCurrencyCode: CurrencyCode;
  baseTaxMonthly: number;
}

export interface SettleReceivableResponse {
  settlementId: string;
}
