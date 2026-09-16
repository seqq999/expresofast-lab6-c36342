package cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.dto;

import java.math.BigDecimal;

import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.domain.Conductor;
import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.domain.Vehiculo;

public class EnvioCreationDTO {
    private Integer id;

    private String codigoRastreo;

    private String direccionDestino;

    private BigDecimal pesoKg;

    private BigDecimal costo;

    private String estadoEnvio;

    private Vehiculo vehiculo;

    private Conductor conductor;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigoRastreo() {
        return codigoRastreo;
    }

    public void setCodigoRastreo(String codigoRastreo) {
        this.codigoRastreo = codigoRastreo;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public void setDireccionDestino(String direccionDestino) {
        this.direccionDestino = direccionDestino;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }

    public String getEstadoEnvio() {
        return estadoEnvio;
    }

    public void setEstadoEnvio(String estadoEnvio) {
        this.estadoEnvio = estadoEnvio;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

}
