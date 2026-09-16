package cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.domain.BitacoraEnvio;

public interface BitacoraEnvioRepository extends JpaRepository<BitacoraEnvio, Integer> {
    List<BitacoraEnvio> findByEnvioIdOrderByFechaCambioDesc(Integer envioId);
}