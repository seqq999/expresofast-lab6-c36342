import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { EnvioFormComponent } from './envio-form.component';

describe('EnvioFormComponent', () => {
  let component: EnvioFormComponent;
  let fixture: ComponentFixture<EnvioFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnvioFormComponent],
      providers: [provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(EnvioFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
