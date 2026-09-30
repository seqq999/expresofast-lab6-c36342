package com.expresofast.expresofast_backend.service;


import java.util.List;

import com.expresofast.expresofast_backend.dto.CrearEnvioDTO;
import com.expresofast.expresofast_backend.dto.EnvioDTO;

public interface EnvioService {

    List<EnvioDTO> obtenerTodos();

    EnvioDTO obtenerPorCodigoRastreo(String codigoRastreo);

    EnvioDTO crearEnvio(CrearEnvioDTO dto);

    EnvioDTO actualizarEstado(Long id, String nuevoEstado);
}