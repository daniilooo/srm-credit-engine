import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { SettlementFormComponent } from './settlement-form.component';

describe('SettlementFormComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettlementFormComponent],
      providers: [provideHttpClient()]
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(SettlementFormComponent);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should reject invalid UUID in receivableId', () => {
    const fixture = TestBed.createComponent(SettlementFormComponent);
    const control = fixture.componentInstance.form.get('receivableId')!;
    control.setValue('not-a-uuid');
    control.markAsTouched();
    expect(control.hasError('pattern')).toBeTrue();
  });

  it('should accept valid UUID in receivableId', () => {
    const fixture = TestBed.createComponent(SettlementFormComponent);
    const control = fixture.componentInstance.form.get('receivableId')!;
    control.setValue('a1b2c3d4-e5f6-7890-abcd-ef1234567890');
    expect(control.hasError('pattern')).toBeFalse();
  });
});
