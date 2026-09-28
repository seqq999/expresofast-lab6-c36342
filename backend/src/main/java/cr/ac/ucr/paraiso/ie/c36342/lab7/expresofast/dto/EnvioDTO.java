package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.*;

public record EnvioDTO(
                @NotNull 
                Integer id,
                
                @Pattern(regexp = "^EXP-\\d{4}$", message = "Formato inválido. Ejemplo: EXP-1234") 
                String codigoRastreo,
                
                @NotBlank(message = "El destinatario es obligatorio")
                 String destinatario,
                
                 @NotBlank(message = "La dirección de destino es obligatoria") 
                String direccionDestino,
                
                @NotNull @PositiveOrZero 
                BigDecimal montoFlete,
                
                @NotNull 
                String estado,
                
                @NotNull 
                LocalDateTime fechaCreacion) {
}