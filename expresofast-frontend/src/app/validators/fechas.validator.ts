import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const fechasValidator: ValidatorFn = (
    control: AbstractControl
): ValidationErrors | null => {
    const fechaDespacho = control.get('fechaDespacho')?.value as string | null;
    const fechaEntregaEstimada = control.get('fechaEntregaEstimada')?.value as string | null;

    if (!fechaDespacho || !fechaEntregaEstimada) {
        return null;
    }

    return fechaEntregaEstimada > fechaDespacho ? null : { fechasInvalidas: true };
};
