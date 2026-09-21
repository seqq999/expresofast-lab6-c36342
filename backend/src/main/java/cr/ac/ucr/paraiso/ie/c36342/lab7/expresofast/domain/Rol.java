package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name = "Rol")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer id;

    @Column(name = "nombre_rol", nullable = false, unique = true)
    private String nombreRol;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Set<Usuario> usuarios = new HashSet<>();

    public Integer getId() { return id; }
    public String getNombreRol() { return nombreRol; }
    public void setNombreRol(String nombreRol) { this.nombreRol = nombreRol; }
}