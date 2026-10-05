import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { EnvioListComponent } from './envio-list.component';

describe('EnvioListComponent', () => {
  let component: EnvioListComponent;
  let fixture: ComponentFixture<EnvioListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EnvioListComponent],
      providers: [provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(EnvioListComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
