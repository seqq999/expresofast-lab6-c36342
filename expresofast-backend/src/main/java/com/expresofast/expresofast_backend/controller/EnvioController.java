package com.expresofast.expresofast_backend.controller;

import java.util.List;

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

import com.expresofast.expresofast_backend.dto.CrearEnvioDTO;
import com.expresofast.expresofast_backend.dto.EnvioDTO;
import com.expresofast.expresofast_backend.service.EnvioService;

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
    public ResponseEntity<EnvioDTO> crear(@Valid @RequestBody CrearEnvioDTO dto) {
        EnvioDTO creado = envioService.crearEnvio(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PatchMapping("/{id}/estado")
    public EnvioDTO actualizarEstado(@PathVariable Long id, @RequestParam String estado) {
        return envioService.actualizarEstado(id, estado);
    }
}