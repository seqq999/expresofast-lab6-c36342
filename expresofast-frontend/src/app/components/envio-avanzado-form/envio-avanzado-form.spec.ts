import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { EnvioAvanzadoFormComponent } from './envio-avanzado-form';

describe('EnvioAvanzadoFormComponent', () => {
  let component: EnvioAvanzadoFormComponent;
  let fixture: ComponentFixture<EnvioAvanzadoFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnvioAvanzadoFormComponent],
      providers: [provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(EnvioAvanzadoFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
