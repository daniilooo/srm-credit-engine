export type CurrencyCode = 'BRL' | 'USD';

export interface RegisterExchangeRateRequest {
  baseCurrency: CurrencyCode;
  quoteCurrency: CurrencyCode;
  rateValue: number;
  validFrom: string;
}

export interface ExchangeRateResponse {
  baseCurrencyCode: string;
  quoteCurrencyCode: string;
  rateValue: number;
  validFrom: string;
  capturedAt: string;
}
