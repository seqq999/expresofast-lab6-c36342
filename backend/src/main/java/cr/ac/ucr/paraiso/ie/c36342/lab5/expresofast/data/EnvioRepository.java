package cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.domain.Envio;

public interface EnvioRepository extends JpaRepository<Envio, Integer> {

    @Query("SELECT e FROM Envio e "
	    + "JOIN FETCH e.vehiculo v "
	    + "JOIN FETCH v.empresa "
	    + "JOIN FETCH e.conductor")
    List<Envio> findAllOptimized();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Envio e SET e.estadoEnvio = :estado "
	    + "WHERE e.vehiculo.id = :vehiculoId")
    int updateEstadoByVehiculoId(@Param("vehiculoId") Integer vehiculoId,
	    @Param("estado") String estado);

}
