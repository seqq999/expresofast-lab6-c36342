import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { EnvioService } from '../../services/envio.service';
import { CrearEnvioPayload } from '../../models/envio.model';

@Component({
  selector: 'app-envio-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './envio-form.component.html',
  styleUrl: './envio-form.component.css'
})
export class EnvioFormComponent {
  private readonly envioService = inject(EnvioService);
  private readonly router = inject(Router);

  payload: CrearEnvioPayload = {
    destinatario: '',
    direccionDestino: '',
    montoFlete: 0
  };

  mensajeExito = '';
  mensajeError = '';

  guardarEnvio(): void {
    if (!this.payload.destinatario || !this.payload.direccionDestino || this.payload.montoFlete <= 0) {
      this.mensajeError = 'Por favor complete todos los campos requeridos con valores válidos.';
      return;
    }

    this.mensajeError = '';
    this.envioService.crearEnvio(this.payload).subscribe({
      next: (res) => {
        this.mensajeExito = `Envío registrado con éxito. Código de Rastreo: ${res.codigoRastreo}`;
        setTimeout(() => this.router.navigate(['/envios']), 2000);
      },
      error: () => {
        this.mensajeError = 'Error al intentar registrar el envío en el servidor.';
      }
    });
  }
}