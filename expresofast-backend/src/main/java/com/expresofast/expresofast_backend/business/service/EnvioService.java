package com.expresofast.expresofast_backend.business.service;

import java.util.List;

import com.expresofast.expresofast_backend.dto.EnvioDTO;
import com.expresofast.expresofast_backend.dto.EnvioRegistroDTO;

public interface EnvioService {

    List<EnvioDTO> obtenerTodos();

    EnvioDTO obtenerPorCodigoRastreo(String codigoRastreo);

    EnvioDTO crearEnvio(EnvioRegistroDTO dto);

    EnvioDTO actualizarEstado(Long id, String nuevoEstado);

    boolean existeCodigoRastreo(String codigoRastreo);
}