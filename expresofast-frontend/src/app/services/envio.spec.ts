import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { EnvioService } from './envio.service';

describe('EnvioService', () => {
  let service: EnvioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [EnvioService, provideHttpClient()]
    });
    service = TestBed.inject(EnvioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
