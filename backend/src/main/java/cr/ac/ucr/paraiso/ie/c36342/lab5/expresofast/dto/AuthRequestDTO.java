package cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(@NotBlank String username, @NotBlank String password) { }