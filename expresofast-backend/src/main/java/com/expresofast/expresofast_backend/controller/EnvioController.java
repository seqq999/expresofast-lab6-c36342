package com.expresofast.expresofast_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.expresofast.expresofast_backend.dto.EnvioDTO;
import com.expresofast.expresofast_backend.dto.EnvioRegistroDTO;
import com.expresofast.expresofast_backend.business.service.EnvioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/envios")
@CrossOrigin(origins = "http://localhost:4200")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    public List<EnvioDTO> obtenerTodos() {
        return envioService.obtenerTodos();
    }

    @GetMapping("/rastreo/{codigo}")
    public EnvioDTO obtenerPorRastreo(@PathVariable String codigo) {
        return envioService.obtenerPorCodigoRastreo(codigo);
    }

    @PostMapping
    public ResponseEntity<EnvioDTO> crear(@Valid @RequestBody EnvioRegistroDTO dto) {
        EnvioDTO creado = envioService.crearEnvio(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PatchMapping("/{id}/estado")
    public EnvioDTO actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        return envioService.actualizarEstado(id, estado);
    }

    @GetMapping("/check-tracking/{trackingNumber}")
    public Map<String, Boolean> checkTracking(@PathVariable String trackingNumber){
        boolean exist = envioService.existeCodigoRastreo(trackingNumber);
        return Map.of("exists", exist);
    }
}