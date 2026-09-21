package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "Conductor")
public class Conductor {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "conductor_id")
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellidos")
    private String apellidos;

    @Column(name = "licencia", unique = true)
    private String licencia;

    @Column(name = "telefono")
    private String telefono;

    @JsonIgnore
    @OneToMany(mappedBy = "conductor")
    private List<Envio> envios;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<Envio> getEnvios() {
        return envios;
    }

    public void setEnvios(List<Envio> envios) {
        this.envios = envios;
    }
}
