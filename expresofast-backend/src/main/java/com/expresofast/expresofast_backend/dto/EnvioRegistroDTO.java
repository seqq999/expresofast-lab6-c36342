package com.expresofast.expresofast_backend.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EnvioRegistroDTO(
        @NotBlank(message = "El número de rastreo es obligatorio")
         String codigoRastreo,

        @NotBlank(message = "El destinatario es obligatorio") 
        String destinatario,

        @NotBlank(message = "La dirección de destino es obligatoria") 
        String direccionDestino,

        @NotNull(message = "El monto del flete es obligatorio") 
        @Positive(message = "El monto del flete debe ser mayor a cero") 
        Double montoFlete,

        @NotNull(message = "La fecha de despacho es obligatoria") 
        LocalDate fechaDespacho,

        @NotNull(message = "La fecha estimada de entrega es obligatoria") 
        LocalDate fechaEntregaEstimada,

        @NotEmpty(message = "Debe registrar al menos un paquete") 
        @Valid 
        List<PaqueteDTO> paquetes) {
}