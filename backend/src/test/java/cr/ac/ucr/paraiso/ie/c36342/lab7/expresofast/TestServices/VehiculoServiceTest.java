package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.TestServices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.VehiculoService;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.exceptions.DuplicateResourceException;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.VehiculoRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Vehiculo;

@ExtendWith(MockitoExtension.class)
class VehiculoServiceTest {

    @Mock
    private VehiculoRepository repo;

    @InjectMocks
    private VehiculoService vehiculoService;

    @Test
    void guardar_VehiculoNuevoPlacaUnica_RetornaVehiculoGuardado() {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca("SJO-1234");
        vehiculo.setCapacidadKg(new BigDecimal("2000"));

        when(repo.existsByPlaca("SJO-1234")).thenReturn(false);
        when(repo.save(vehiculo)).thenReturn(vehiculo);

        Vehiculo resultado = vehiculoService.guardar(vehiculo);

        assertEquals("SJO-1234", resultado.getPlaca());
        verify(repo).save(vehiculo);
    }

    @Test
    void guardar_VehiculoNuevoPlacaDuplicada_LanzaExcepcion() {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca("SJO-1234");

        when(repo.existsByPlaca("SJO-1234")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> vehiculoService.guardar(vehiculo));

        verify(repo, never()).save(vehiculo);
    }

    @Test
    void guardar_VehiculoExistenteConId_NoValidaPlacaDuplicada() {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(5);
        vehiculo.setPlaca("SJO-1234");

        when(repo.save(vehiculo)).thenReturn(vehiculo);

        Vehiculo resultado = vehiculoService.guardar(vehiculo);

        assertEquals(5, resultado.getId());
        verify(repo, never()).existsByPlaca(vehiculo.getPlaca());
        verify(repo).save(vehiculo);
    }

    @Test
    void listar_RetornaListaDeVehiculos() {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1);

        when(repo.findAll()).thenReturn(List.of(vehiculo));

        List<Vehiculo> resultado = vehiculoService.listar();

        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorId_VehiculoExistente_RetornaOptionalConValor() {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setId(1);

        when(repo.findById(1)).thenReturn(Optional.of(vehiculo));

        Optional<Vehiculo> resultado = vehiculoService.buscarPorId(1);

        assertTrue(resultado.isPresent());
    }

    @Test
    void buscarPorId_VehiculoNoExistente_RetornaOptionalVacio() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        Optional<Vehiculo> resultado = vehiculoService.buscarPorId(99);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void eliminar_LlamaAlRepositorio() {
        vehiculoService.eliminar(1);

        verify(repo).deleteById(1);
    }
}