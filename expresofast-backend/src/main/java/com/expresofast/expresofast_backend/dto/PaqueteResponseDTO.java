package com.expresofast.expresofast_backend.dto;

import java.math.BigDecimal;

public record PaqueteResponseDTO(
        Long id,
        String descripcion,
        BigDecimal pesoKg) {
}