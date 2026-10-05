import { AbstractControl, AsyncValidatorFn } from '@angular/forms';
import { of } from 'rxjs';
import { catchError, debounceTime, map, switchMap } from 'rxjs/operators';
import { EnvioService } from '../services/envio.service';

export function trackingDuplicadoValidator(envioService: EnvioService): AsyncValidatorFn {
    return (control: AbstractControl) => {
        if (!control.value) {
            return of(null);
        }

        return of(control.value).pipe(
            debounceTime(400),
            switchMap(codigo => envioService.checkTracking(codigo)),
            map(respuesta => (respuesta.exists ? { trackingTomado: true } : null)),
            catchError(() => of(null))
        );
    };
}