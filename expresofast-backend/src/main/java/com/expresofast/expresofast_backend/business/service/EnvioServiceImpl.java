package com.expresofast.expresofast_backend.business.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expresofast.expresofast_backend.dto.EnvioDTO;
import com.expresofast.expresofast_backend.dto.EnvioRegistroDTO;
import com.expresofast.expresofast_backend.dto.PaqueteDTO;
import com.expresofast.expresofast_backend.dto.PaqueteResponseDTO;
import com.expresofast.expresofast_backend.business.exceptions.TrackingDuplicadoException;
import com.expresofast.expresofast_backend.model.Envio;
import com.expresofast.expresofast_backend.model.Paquete;
import com.expresofast.expresofast_backend.repository.EnvioRepository;

@Service
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;

    public EnvioServiceImpl(EnvioRepository envioRepository) {
        this.envioRepository = envioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvioDTO> obtenerTodos() {
        return envioRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EnvioDTO obtenerPorCodigoRastreo(String codigoRastreo) {
        Envio envio = envioRepository.findByCodigoRastreo(codigoRastreo)
                .orElseThrow(() -> new NoSuchElementException(
                        "No existe un envío con el código de rastreo " + codigoRastreo));
        return mapToDTO(envio);
    }

    @Override
    @Transactional
    public EnvioDTO crearEnvio(EnvioRegistroDTO dto) {
        if (envioRepository.existsByCodigoRastreo(dto.codigoRastreo())) {
            throw new TrackingDuplicadoException(
                    "El número de rastreo " + dto.codigoRastreo() + " ya está en uso");
        }

        Envio envio = new Envio(
                dto.codigoRastreo(),
                dto.destinatario(),
                dto.direccionDestino(),
                dto.montoFlete(),
                "PENDIENTE",
                LocalDateTime.now());
        envio.setFechaDespacho(dto.fechaDespacho());
        envio.setFechaEntregaEstimada(dto.fechaEntregaEstimada());

        for (PaqueteDTO paqueteDto : dto.paquetes()) {
            Paquete paquete = new Paquete(
                    null,
                    paqueteDto.descripcion(),
                    paqueteDto.pesoKg());
            envio.addPaquete(paquete);
        }

        Envio guardado = envioRepository.save(envio);
        return mapToDTO(guardado);
    }

    @Override
    @Transactional
    public EnvioDTO actualizarEstado(Long id, String nuevoEstado) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No existe un envío con id " + id));

        envio.setEstado(nuevoEstado);
        Envio actualizado = envioRepository.save(envio);
        return mapToDTO(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCodigoRastreo(String codigoRastreo) {
        return envioRepository.existsByCodigoRastreo(codigoRastreo);
    }

    private EnvioDTO mapToDTO(Envio envio) {
        List<PaqueteResponseDTO> paquetesDto = envio.getPaquetes().stream()
                .map(p -> new PaqueteResponseDTO(p.getId(), p.getDescripcion(), p.getPesoKg()))
                .toList();

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