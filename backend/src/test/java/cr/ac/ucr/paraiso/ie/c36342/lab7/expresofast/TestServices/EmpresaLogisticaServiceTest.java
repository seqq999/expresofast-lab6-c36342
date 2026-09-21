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

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.EmpresaLogisticaService;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.EmpresaLogisticaRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.EmpresaLogistica;

@ExtendWith(MockitoExtension.class)
class EmpresaLogisticaServiceTest {

    @Mock
    private EmpresaLogisticaRepository repo;

    @InjectMocks
    private EmpresaLogisticaService empresaService;

    @Test
    void findAll_RetornaListaDeEmpresas() {
        EmpresaLogistica empresa = new EmpresaLogistica();
        empresa.setId(1);
        empresa.setNombre("ExpresoFast Transportes");

        when(repo.findAll()).thenReturn(List.of(empresa));

        List<EmpresaLogistica> resultado = empresaService.findAll();

        assertEquals(1, resultado.size());
        assertEquals("ExpresoFast Transportes", resultado.get(0).getNombre());
    }

    @Test
    void buscarPorId_EmpresaExistente_RetornaOptionalConValor() {
        EmpresaLogistica empresa = new EmpresaLogistica();
        empresa.setId(1);

        when(repo.findById(1)).thenReturn(Optional.of(empresa));

        Optional<EmpresaLogistica> resultado = empresaService.buscarPorId(1);

        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().getId());
    }

    @Test
    void buscarPorId_EmpresaNoExistente_RetornaOptionalVacio() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        Optional<EmpresaLogistica> resultado = empresaService.buscarPorId(99);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void guardar_EmpresaValida_RetornaEmpresaGuardada() {
        EmpresaLogistica empresa = new EmpresaLogistica();
        empresa.setNombre("Nueva Empresa");

        when(repo.save(empresa)).thenReturn(empresa);

        EmpresaLogistica resultado = empresaService.guardar(empresa);

        assertEquals("Nueva Empresa", resultado.getNombre());
        verify(repo).save(empresa);
    }

    @Test
    void eliminar_LlamaAlRepositorio() {
        empresaService.eliminar(1);

        verify(repo).deleteById(1);
    }
}