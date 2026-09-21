package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

@Entity
@Table(name = "Usuario")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Integer id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "UsuarioRol", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles = new HashSet<>();

    public Integer getId() { return id; }
    @Override public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    @Override public String getPassword() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail() { return email; }
    public Boolean getActivo() { return activo; }
    public Set<Rol> getRoles() { return roles; }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(rol -> new SimpleGrantedAuthority(rol.getNombreRol())).collect(Collectors.toSet());
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return Boolean.TRUE.equals(activo); }
}