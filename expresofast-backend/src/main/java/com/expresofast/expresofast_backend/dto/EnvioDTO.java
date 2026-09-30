package com.expresofast.expresofast_backend.dto;

import java.time.LocalDateTime;

public record EnvioDTO(
                Long id,
                String codigoRastreo,
                String destinatario,
                String direccionDestino,
                Double montoFlete,
                String estado,
                LocalDateTime fechaCreacion) {
}