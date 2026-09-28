package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Envio;

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

    @Procedure(procedureName = "SP_OBTENER_ENVIOS_POR_ESTADO")
    List<Envio> obtenerEnviosPorEstado(@Param("pEstado") String pEstado);

    Page<Envio> findByEstadoEnvio(String estado, Pageable pageable);

    @EntityGraph(attributePaths = { "vehiculo", "conductor", "vehiculo.empresa" })
    @Query("""
            SELECT e FROM Envio e
            WHERE (:estado IS NULL OR e.estadoEnvio = :estado)
                AND(:busqueda IS NULL OR
                    LOWER(e.codigoRastreo) LIKE LOWER(CONCAT('%', :busqueda, '%'))OR
                    LOWER(e.destinatario) LIKE LOWER(CONCAT('%', :busqueda, '%'))OR
                    LOWER(e.direccionDestino) LIKE LOWER(CONCAT('%', :busqueda, '%')))
            """)
    Page<Envio> buscarPaginado(@Param("estado") String estado,
            @Param("busqueda") String busqueda,
            Pageable pageable);
}
