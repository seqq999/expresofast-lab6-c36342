package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.TestServices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.ConductorService;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.ConductorRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Conductor;

@ExtendWith(MockitoExtension.class)
class ConductorServiceTest {

    @Mock
    private ConductorRepository repo;

    @InjectMocks
    private ConductorService conductorService;

    @Test
    void findAll_RetornaListaDeConductores() {
        Conductor conductor = new Conductor();
        conductor.setId(1);
        conductor.setNombre("Carlos");

        when(repo.findAll()).thenReturn(List.of(conductor));

        List<Conductor> resultado = conductorService.findAll();

        assertEquals(1, resultado.size());
        assertEquals("Carlos", resultado.get(0).getNombre());
    }

    @Test
    void findById_ConductorExistente_RetornaOptionalConValor() {
        Conductor conductor = new Conductor();
        conductor.setId(1);

        when(repo.findById(1)).thenReturn(Optional.of(conductor));

        Optional<Conductor> resultado = conductorService.findById(1);

        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getId());
    }

    @Test
    void findById_ConductorNoExistente_RetornaOptionalVacio() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        Optional<Conductor> resultado = conductorService.findById(99);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void save_ConductorValido_RetornaConductorGuardado() {
        Conductor conductor = new Conductor();
        conductor.setNombre("Ana");

        when(repo.save(conductor)).thenReturn(conductor);

        Conductor resultado = conductorService.save(conductor);

        assertEquals("Ana", resultado.getNombre());
        verify(repo).save(conductor);
    }

    @Test
    void deleteById_LlamaAlRepositorio() {
        conductorService.deleteById(1);

        verify(repo).deleteById(1);
    }
}