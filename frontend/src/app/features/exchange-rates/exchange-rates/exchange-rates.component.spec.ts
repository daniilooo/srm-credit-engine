import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { ExchangeRatesComponent } from './exchange-rates.component';

describe('ExchangeRatesComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExchangeRatesComponent],
      providers: [provideHttpClient()]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(ExchangeRatesComponent);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should have registerForm invalid when rateValue is missing', () => {
    const fixture = TestBed.createComponent(ExchangeRatesComponent);
    const form = fixture.componentInstance.registerForm;
    form.patchValue({ baseCurrency: 'BRL', quoteCurrency: 'USD', validFrom: '2026-06-21T10:00' });
    expect(form.invalid).toBeTrue();
  });

  it('should start with no loading state', () => {
    const fixture = TestBed.createComponent(ExchangeRatesComponent);
    expect(fixture.componentInstance.registerLoading()).toBeFalse();
    expect(fixture.componentInstance.findLoading()).toBeFalse();
  });
});
