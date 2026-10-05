import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EnvioService } from '../../services/envio.service';
import { Envio, EstadoEnvio } from '../../models/envio.model';

@Component({
  selector: 'app-envio-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './envio-list.component.html',
  styleUrl: './envio-list.component.css'
})
export class EnvioListComponent implements OnInit {
  private readonly envioService = inject(EnvioService);
  private readonly cdr = inject(ChangeDetectorRef); 

  envios: Envio[] = [];
  estados: EstadoEnvio[] = ['PENDIENTE', 'EN_TRANSITO', 'ENTREGADO', 'CANCELADO'];
  cargando = true;
  mensajeError = '';

  ngOnInit(): void {
    this.cargarEnvios();
  }

  cargarEnvios(): void {
    this.cargando = true;
    this.envioService.obtenerEnvios().subscribe({
      next: (data) => {
        this.envios = data;
        this.cargando = false;
        this.cdr.detectChanges();
            },
      error: (err) => {
        this.mensajeError = 'Error al cargar la lista de envíos.';
        this.cargando = false;
        this.cdr.detectChanges(); 
      }
    });
  }

  cambiarEstado(envio: Envio, nuevoEstado: string): void {
    this.envioService.actualizarEstado(envio.id, nuevoEstado).subscribe({
      next: (envioActualizado) => {
        envio.estado = envioActualizado.estado;
      },
      error: () => alert('Ocurrió un error al actualizar el estado.')
    });
  }

  obtenerClaseInsignia(estado: EstadoEnvio): string {
    switch (estado) {
      case 'PENDIENTE': return 'badge-pendiente';
      case 'EN_TRANSITO': return 'badge-transito';
      case 'ENTREGADO': return 'badge-entregado';
      case 'CANCELADO': return 'badge-cancelado';
      default: return '';
    }
  }
}