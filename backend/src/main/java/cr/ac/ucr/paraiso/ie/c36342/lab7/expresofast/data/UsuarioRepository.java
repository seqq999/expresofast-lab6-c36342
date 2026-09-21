package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
}