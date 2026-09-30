package com.expresofast.expresofast_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expresofast.expresofast_backend.model.Envio;

public interface EnvioRepository extends JpaRepository<Envio, Long> {

    Optional<Envio> findByCodigoRastreo(String codigoRastreo);

    List<Envio> findByEstado(String estado);

    boolean existsByCodigoRastreo(String codigoRastreo);
}
