package com.expresofast.expresofast_backend.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "Envios")
public class Envio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "envio_id")
    private Long id;

    @Column(name = "codigo_rastreo")
    private String codigoRastreo;

    @Column(name = "destinatario")
    private String destinatario;

    @Column(name = "direccion_destino")
    private String direccionDestino;

    @Column(name = "monto_flete")
    private Double montoFlete;

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_despacho")
    private LocalDate fechaDespacho;

    @Column(name = "fecha_entrega_estimada")
    private LocalDate fechaEntregaEstimada;

    @OneToMany(mappedBy = "envio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Paquete> paquetes = new ArrayList<>();

    public Envio() {
    }

    public Envio(String codigoRastreo, String destinatario, String direccionDestino,
            Double montoFlete, String estado, LocalDateTime fechaCreacion) {
        this.codigoRastreo = codigoRastreo;
        this.destinatario = destinatario;
        this.direccionDestino = direccionDestino;
        this.montoFlete = montoFlete;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoRastreo() {
        return codigoRastreo;
    }

    public void setCodigoRastreo(String codigoRastreo) {
        this.codigoRastreo = codigoRastreo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
    }

    public Double getMontoFlete() {
        return montoFlete;
    }

    public void setMontoFlete(Double montoFlete) {
        this.montoFlete = montoFlete;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDate getFechaDespacho() {
        return fechaDespacho;
    }

    public void setFechaDespacho(LocalDate fechaDespacho) {
        this.fechaDespacho = fechaDespacho;
    }

    public LocalDate getFechaEntregaEstimada() {
        return fechaEntregaEstimada;
    }

    public void setFechaEntregaEstimada(LocalDate fechaEntregaEstimada) {
        this.fechaEntregaEstimada = fechaEntregaEstimada;
    }

    public List<Paquete> getPaquetes() {
        return paquetes;
    }

    public void setPaquetes(List<Paquete> paquetes) {
        this.paquetes = paquetes;
    }

    public void addPaquete(Paquete paquete) {
        paquetes.add(paquete);
        paquete.setEnvio(this);
    }

    public void removePaquete(Paquete paquete) {
        paquetes.remove(paquete);
        paquete.setEnvio(null);
    }
}