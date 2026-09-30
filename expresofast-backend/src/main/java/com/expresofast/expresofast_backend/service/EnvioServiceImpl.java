package com.expresofast.expresofast_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.expresofast.expresofast_backend.dto.CrearEnvioDTO;
import com.expresofast.expresofast_backend.dto.EnvioDTO;
import com.expresofast.expresofast_backend.model.Envio;
import com.expresofast.expresofast_backend.repository.EnvioRepository;

@Service
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;
    private final Random random = new Random();

    public EnvioServiceImpl(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    @Override
    public List<EnvioDTO> obtenerTodos() {
        return envioRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public EnvioDTO obtenerPorCodigoRastreo(String codigoRastreo) {
        Envio envio = envioRepository.findByCodigoRastreo(codigoRastreo)
                .orElseThrow(() -> new NoSuchElementException(
                        "No existe un envío con el código de rastreo " + codigoRastreo));
        return mapToDTO(envio);
    }

    @Override
    public EnvioDTO crearEnvio(CrearEnvioDTO dto) {
        Envio envio = new Envio(
                generarCodigoRastreoUnico(),
                dto.destinatario(),
                dto.direccionDestino(),
                dto.montoFlete(),
                "PENDIENTE",
                LocalDateTime.now());

        Envio guardado = envioRepository.save(envio);
        return mapToDTO(guardado);
    }

    @Override
    public EnvioDTO actualizarEstado(Long id, String nuevoEstado) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe un envío con id " + id));

        envio.setEstado(nuevoEstado);
        Envio actualizado = envioRepository.save(envio);
        return mapToDTO(actualizado);
    }

    private String generarCodigoRastreoUnico() {
        String codigo;
        do {
            int numero = 1000 + random.nextInt(9000); // 4 dígitos, entre 1000 y 9999
            codigo = "EXP-2026-" + numero;
        } while (envioRepository.existsByCodigoRastreo(codigo));
        return codigo;
    }

    private EnvioDTO mapToDTO(Envio envio) {
        return new EnvioDTO(
                envio.getId(),
                envio.getCodigoRastreo(),
                envio.getDestinatario(),
                envio.getDireccionDestino(),
                envio.getMontoFlete(),
                envio.getEstado(),
                envio.getFechaCreacion());
    }

}