package cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.dto;

import java.math.BigDecimal;

public class EnvioResponseDTO {
    private Integer id;
    private String codigoRastreo;
    private String direccionDestino;
    private BigDecimal pesoKg;
    private BigDecimal costo;
    private String estadoEnvio;
    private Integer vehiculoId;
    private Integer conductorId;
    private Integer empresaId;
    private String empresaNombre;

    public EnvioResponseDTO(
            Integer id,
            String codigoRastreo,
            String direccionDestino,
            BigDecimal pesoKg,
            BigDecimal costo,
            String estadoEnvio,
            Integer vehiculoId,
            Integer conductorId,
            Integer empresaId,
            String empresaNombre) {
        this.id = id;
        this.codigoRastreo = codigoRastreo;
        this.direccionDestino = direccionDestino;
        this.pesoKg = pesoKg;
        this.costo = costo;
        this.estadoEnvio = estadoEnvio;
        this.vehiculoId = vehiculoId;
        this.conductorId = conductorId;
        this.empresaId = empresaId;
        this.empresaNombre = empresaNombre;
    }

    public Integer getId() {
        return id;
    }

    public String getCodigoRastreo() {
        return codigoRastreo;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public String getEstadoEnvio() {
        return estadoEnvio;
    }

    public Integer getVehiculoId() {
        return vehiculoId;
    }

    public Integer getConductorId() {
        return conductorId;
    }

    public Integer getEmpresaId() {
        return empresaId;
    }

    public String getEmpresaNombre() {
        return empresaNombre;
    }
}
