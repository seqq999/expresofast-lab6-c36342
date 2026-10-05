import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
    CheckTrackingResponse,
    CrearEnvioPayload,
    Envio,
    EnvioRegistroPayload
} from '../models/envio.model';

@Injectable({
    providedIn: 'root'
})
export class EnvioService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = `${environment.apiUrl}envios`;

    obtenerEnvios(): Observable<Envio[]> {
        return this.http.get<Envio[]>(this.baseUrl);
    }

    obtenerPorRastreo(codigo: string): Observable<Envio> {
        return this.http.get<Envio>(`${this.baseUrl}/rastreo/${encodeURIComponent(codigo)}`);
    }

    crearEnvio(payload: CrearEnvioPayload): Observable<Envio> {
        return this.http.post<Envio>(this.baseUrl, payload);
    }

    actualizarEstado(id: number, nuevoEstado: string): Observable<Envio> {
        return this.http.patch<Envio>(`${this.baseUrl}/${id}/estado`, null, {
            params: { estado: nuevoEstado }
        });
    }
    registrarEnvioAvanzado(payload: EnvioRegistroPayload): Observable<Envio> {
        return this.http.post<Envio>(this.baseUrl, payload);
    }

    checkTracking(codigo: string): Observable<CheckTrackingResponse> {
        return this.http.get<CheckTrackingResponse>(
            `${this.baseUrl}/check-tracking/${encodeURIComponent(codigo)}`
        );
    }
}