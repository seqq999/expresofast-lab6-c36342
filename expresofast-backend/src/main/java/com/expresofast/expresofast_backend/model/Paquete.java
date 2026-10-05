package com.expresofast.expresofast_backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "PAQUETES")
public class Paquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "peso_kg")
    private BigDecimal peso_kg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "envio_id")
    private Envio envio;

    public Paquete() {
    }

    public Paquete(Envio envio, String descripcion, BigDecimal peso_kg) {
        this.envio = envio;
        this.descripcion = descripcion;
        this.peso_kg = peso_kg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPesoKg() {
        return peso_kg;
    }

    public void setPesoKg(BigDecimal peso_kg) {
        this.peso_kg = peso_kg;
    }

    public Envio getEnvio() {
        return envio;
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
    }
}