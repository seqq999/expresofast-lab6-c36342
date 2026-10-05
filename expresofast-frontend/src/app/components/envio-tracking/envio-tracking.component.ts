import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnvioService } from '../../services/envio.service';
import { Envio } from '../../models/envio.model';

@Component({
  selector: 'app-envio-tracking',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './envio-tracking.component.html',
  styleUrl: './envio-tracking.component.css'
})
export class EnvioTrackingComponent {
  private readonly envioService = inject(EnvioService);

  codigoBusqueda = '';
  envioEncontrado: Envio | null = null;
  mensajeError = '';
  buscando = false;

  buscarGuia(): void {
    if (!this.codigoBusqueda.trim()) {
      this.mensajeError = 'Ingrese un código de rastreo válido.';
      return;
    }

    this.buscando = true;
    this.mensajeError = '';
    this.envioEncontrado = null;

    this.envioService.obtenerPorRastreo(this.codigoBusqueda.trim()).subscribe({
      next: (data) => {
        this.envioEncontrado = data;
        this.buscando = false;
      },
      error: () => {
        this.mensajeError = `No se encontró ningún paquete con la guía: ${this.codigoBusqueda}`;
        this.buscando = false;
      }
    });
  }

  obtenerPorcentajeProgreso(estado: string | undefined): number {
    switch (estado) {
      case 'PENDIENTE': return 25;
      case 'EN_TRANSITO': return 65;
      case 'ENTREGADO': return 100;
      case 'CANCELADO': return 0;
      default: return 0;
    }
  }
}