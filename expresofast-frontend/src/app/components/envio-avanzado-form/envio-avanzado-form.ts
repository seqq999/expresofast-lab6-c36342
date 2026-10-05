import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import { Router } from '@angular/router';

import { EnvioService } from '../../services/envio.service';
import { EnvioRegistroPayload } from '../../models/envio.model';
import { fechasValidator } from '../../validators/fechas.validator';
import { trackingDuplicadoValidator } from '../../validators/tracking-duplicado.validator';

@Component({
  selector: 'app-envio-avanzado-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './envio-avanzado-form.html',
  styleUrl: './envio-avanzado-form.css'
})
export class EnvioAvanzadoFormComponent {
  private fb = inject(FormBuilder).nonNullable;
  private envioService = inject(EnvioService);
  private router = inject(Router);

  mensajeExito = '';
  mensajeError = '';
  enviando = false;

  form = this.fb.group(
    {
      codigoRastreo: this.fb.control('', {
        validators: [Validators.required],
        asyncValidators: [trackingDuplicadoValidator(this.envioService)]
      }),
      destinatario: ['', Validators.required],
      direccionDestino: ['', Validators.required],
      montoFlete: [0, [Validators.required, Validators.min(0.01)]],
      fechaDespacho: ['', Validators.required],
      fechaEntregaEstimada: ['', Validators.required],
      paquetes: this.fb.array([this.crearPaqueteGroup()])
    },
    { validators: fechasValidator }
  );

  get paquetes() {
    return this.form.controls.paquetes;
  }

  crearPaqueteGroup() {
    return this.fb.group({
      descripcion: ['', Validators.required],
      pesoKg: [0, [Validators.required, Validators.min(0.01)]]
    });
  }

  agregarPaquete(): void {
    this.paquetes.push(this.crearPaqueteGroup());
  }

  eliminarPaquete(index: number): void {
    if (this.paquetes.length > 1) {
      this.paquetes.removeAt(index);
    }
  }

  onSubmit(): void {
    this.mensajeExito = '';
    this.mensajeError = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const valores = this.form.getRawValue();
    const payload: EnvioRegistroPayload = {
      codigoRastreo: valores.codigoRastreo,
      destinatario: valores.destinatario,
      direccionDestino: valores.direccionDestino,
      montoFlete: valores.montoFlete,
      fechaDespacho: valores.fechaDespacho,
      fechaEntregaEstimada: valores.fechaEntregaEstimada,
      paquetes: valores.paquetes
    };

    this.enviando = true;
    this.envioService.registrarEnvioAvanzado(payload).subscribe({
      next: () => {
        this.enviando = false;
        this.mensajeExito = 'Envío registrado correctamente.';
        setTimeout(() => this.router.navigate(['/envios']), 1200);
      },
      error: (err) => {
        this.enviando = false;
        this.mensajeError = err.error?.error || 'No se pudo registrar el envío.';
      }
    });
  }
}