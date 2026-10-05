export type EstadoEnvio = 'PENDIENTE' | 'EN_TRANSITO' | 'ENTREGADO' | 'CANCELADO';

export interface Envio {
    id: number;
    codigoRastreo: string;
    destinatario: string;
    direccionDestino: string;
    montoFlete: number;
    estado: EstadoEnvio;
    fechaDespacho?: string;
    fechaEntregaEstimada?: string;
    paquetes?: Paquete[];
}

export interface CrearEnvioPayload {
    destinatario: string;
    direccionDestino: string;
    montoFlete: number;
}

export interface Paquete {
    id?: number;
    descripcion: string;
    pesoKg: number;
}

export interface EnvioRegistroPayload {
    codigoRastreo: string;
    destinatario: string;
    direccionDestino: string;
    montoFlete: number;
    fechaDespacho: string;//formato 'YYYY-MM-DD'
    fechaEntregaEstimada: string; //formato 'YYYY-MM-DD'
    paquetes: Paquete[];
}

export interface CheckTrackingResponse {
    exists: boolean;
}