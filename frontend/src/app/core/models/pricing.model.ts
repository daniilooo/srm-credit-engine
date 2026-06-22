export interface PricingSimulationRequest {
  faceValue: number;
  dueDate: string;
  receivableType: string;
  baseTaxMonthly: number;
}

export interface PricingSimulationResponse {
  presentValue: number;
  appliedTax: number;
  appliedSpread: number;
  termInMonths: number;
}
