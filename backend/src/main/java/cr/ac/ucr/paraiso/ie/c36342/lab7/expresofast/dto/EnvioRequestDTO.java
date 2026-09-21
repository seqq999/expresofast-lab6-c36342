package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

public record EnvioRequestDTO(
                @NotBlank(message = "El código de rastreo es obligatorio") @Pattern(regexp = "^EXP-\\d{4}$", message = "Formato inválido. Ejemplo: EXP-1234") String codigoRastreo,
                @NotBlank(message = "La dirección de destino es obligatoria") String direccionDestino,
                @NotNull @Positive(message = "El peso debe ser mayor a cero") BigDecimal pesoKg,
                @NotNull @PositiveOrZero BigDecimal costo,
                @NotNull Integer vehiculoId,
                @NotNull Integer conductorId) {
}