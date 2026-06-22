import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { PricingSimulationComponent } from './pricing-simulation.component';

describe('PricingSimulationComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PricingSimulationComponent],
      providers: [provideHttpClient()]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(PricingSimulationComponent);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should have invalid form initially', () => {
    const fixture = TestBed.createComponent(PricingSimulationComponent);
    expect(fixture.componentInstance.form.invalid).toBeTrue();
  });

  it('should start with loading false', () => {
    const fixture = TestBed.createComponent(PricingSimulationComponent);
    expect(fixture.componentInstance.loading()).toBeFalse();
  });
});
