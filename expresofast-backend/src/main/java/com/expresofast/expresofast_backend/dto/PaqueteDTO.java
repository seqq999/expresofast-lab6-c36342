package com.expresofast.expresofast_backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaqueteDTO(
        @NotBlank(message = "La descripción del paquete es obligatoria") 
        String descripcion,

        @NotNull(message = "El peso del paquete es obligatorio") 
        @Positive(message = "El peso debe ser mayor a cero") 
        BigDecimal pesoKg) {
}