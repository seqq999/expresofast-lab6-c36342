package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.dto;

import java.time.LocalDateTime;

public record BitacoraResponseDTO(Integer id, String estadoAnterior, String estadoNuevo,
        LocalDateTime fechaCambio, String usuario, String observaciones) { }