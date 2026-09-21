package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "Vehiculo")
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehiculo_id")
    private Integer id;

    @Column(name = "placa")
    private String placa;

    @Column(name = "capacidad_kg")
    private BigDecimal capacidadKg;

    @Column(name = "estado")
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private EmpresaLogistica empresa;

    @JsonIgnore
    @OneToMany(mappedBy = "vehiculo")
    private List<Envio> envios;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public BigDecimal getCapacidadKg() {
        return capacidadKg;
    }

    public void setCapacidadKg(BigDecimal capacidadKg) {
        this.capacidadKg = capacidadKg;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public EmpresaLogistica getEmpresa() {
        return empresa;
    }

    public void setEmpresa(EmpresaLogistica empresa) {
        this.empresa = empresa;
    }

    public List<Envio> getEnvios() {
        return envios;
    }

    public void setEnvios(List<Envio> envios) {
        this.envios = envios;
    }
}
