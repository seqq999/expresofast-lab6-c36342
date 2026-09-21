package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.TestServices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.EnvioService;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.CapacidadExcedidaException;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.EnvioException;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.InvalidStateTransitionException;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.ResourceNotFoundException;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.BitacoraEnvioRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.ConductorRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.EnvioRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.UsuarioRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.data.VehiculoRepository;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.BitacoraEnvio;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Conductor;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Envio;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Usuario;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Vehiculo;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.dto.BitacoraResponseDTO;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.dto.EnvioRequestDTO;

@ExtendWith(MockitoExtension.class)
public class EnvioServiceTest {

        @Mock
        private EnvioRepository envioRepository;

        @Mock
        private VehiculoRepository vehiculoRepository;

        @Mock
        private ConductorRepository conductorRepository;

        @Mock
        private BitacoraEnvioRepository bitacoraRepository;

        @Mock
        private UsuarioRepository usuarioRepository;

        @InjectMocks
        private EnvioService envioService;

        @Test
        void crearEnvio_DatosValidos_RetornaEnvioDTO() {
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setId(1);
                vehiculo.setCapacidadKg(new BigDecimal("20"));

                Conductor conductor = new Conductor();
                conductor.setId(2);

                EnvioRequestDTO request = new EnvioRequestDTO(
                                "EXP-1234",
                                "San Jose",
                                new BigDecimal("10"),
                                new BigDecimal("2500"),
                                1,
                                2);

                when(vehiculoRepository.findById(1))
                                .thenReturn(Optional.of(vehiculo));
                when(conductorRepository.findById(2))
                                .thenReturn(Optional.of(conductor));
                when(envioRepository.save(any(Envio.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Envio resultado = envioService.save(request);

                assertEquals("EXP-1234", resultado.getCodigoRastreo());
                assertEquals("PENDIENTE", resultado.getEstadoEnvio());
                assertEquals(vehiculo, resultado.getVehiculo());
                assertEquals(conductor, resultado.getConductor());
        }

        @Test
        void crearEnvio_VehiculoSinCapacidad_LanzaExcepcion() {
                // capacidad max del carro con id(1)= 1500.00
                Vehiculo carro = new Vehiculo();
                carro.setId(1);
                carro.setCapacidadKg(new BigDecimal("1500.00"));

                Conductor chofer = new Conductor();
                chofer.setId(2);

                EnvioRequestDTO request = new EnvioRequestDTO(
                                "EXP-1234",
                                "San Jose",
                                new BigDecimal("1500.01"),
                                new BigDecimal("2500"),
                                1,
                                2);

                when(vehiculoRepository.findById(1))
                                .thenReturn(Optional.of(carro));
                when(conductorRepository.findById(2))
                                .thenReturn(Optional.of(chofer));

                assertThrows(CapacidadExcedidaException.class,
                                () -> envioService.save(request));

        }

        @Test
        void actualizarEstado_TransicionInvalida_LanzaExcepcion() {
                Envio envio = new Envio();
                envio.setCodigoRastreo("EXP-1234");
                envio.setEstadoEnvio("ENTREGADO");
                when(envioRepository.findById(1)).thenReturn(Optional.of(envio));

                assertThrows(InvalidStateTransitionException.class,
                                () -> envioService.updatedState(1, "EN_TRANSITO", "Reversión no permitida"));
        }

        @Test
        void cancelarEnvio_EnvioEnTransito_LanzaExcepcion() {
                Envio envio = new Envio();
                envio.setCodigoRastreo("EXP-1234");
                envio.setEstadoEnvio("EN_TRANSITO");
                when(envioRepository.findById(1)).thenReturn(Optional.of(envio));

                assertThrows(InvalidStateTransitionException.class,
                                () -> envioService.cancelarEnvio(1));
        }

        @ParameterizedTest
        @CsvSource({
                        "5.0, 10.0, 2500.0",
                        "15.0, 50.0, 7500.0",
                        "100.0, 2.5, 12000.0"
        })
        @DisplayName("Debe calcular la tarifa correcta segun peso y distancia")
        void calcularTarifa_CasosVariados_CalculaCorrectamente(
                        double pesoKg, double distanciaKm, double tarifaEsperada) {
                double tarifaCalculada = envioService.calcularTarifa(
                                pesoKg, distanciaKm);
                assertEquals(tarifaEsperada, tarifaCalculada, 0.01);
        }

        @Test
        void crearEnvio_VehiculoIdNulo_LanzaExcepcion() {
                EnvioRequestDTO request = new EnvioRequestDTO(
                                "EXP-1234", "San Jose", new BigDecimal("10"), new BigDecimal("2500"), null, 2);

                assertThrows(EnvioException.class,
                                () -> envioService.save(request));
        }

        @Test
        void crearEnvio_ConductorIdNulo_LanzaExcepcion() {
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setId(1);
                vehiculo.setCapacidadKg(new BigDecimal("20"));

                EnvioRequestDTO request = new EnvioRequestDTO(
                                "EXP-1234", "San Jose", new BigDecimal("10"), new BigDecimal("2500"), 1, null);

                when(vehiculoRepository.findById(1)).thenReturn(Optional.of(vehiculo));

                assertThrows(EnvioException.class,
                                () -> envioService.save(request));
        }

        @Test
        void crearEnvio_VehiculoInexistente_LanzaExcepcion() {
                EnvioRequestDTO request = new EnvioRequestDTO(
                                "EXP-1234", "San Jose", new BigDecimal("10"), new BigDecimal("2500"), 1, 2);

                when(vehiculoRepository.findById(1)).thenReturn(Optional.empty());

                assertThrows(EnvioException.class,
                                () -> envioService.save(request));
        }

        @Test
        void crearEnvio_ConductorInexistente_LanzaExcepcion() {
                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setId(1);
                vehiculo.setCapacidadKg(new BigDecimal("20"));

                EnvioRequestDTO request = new EnvioRequestDTO(
                                "EXP-1234", "San Jose", new BigDecimal("10"), new BigDecimal("2500"), 1, 2);

                when(vehiculoRepository.findById(1)).thenReturn(Optional.of(vehiculo));
                when(conductorRepository.findById(2)).thenReturn(Optional.empty());

                assertThrows(EnvioException.class,
                                () -> envioService.save(request));
        }

        @Test
        void actualizarEstado_EstadoInvalido_LanzaExcepcion() {
                assertThrows(EnvioException.class,
                                () -> envioService.updatedState(1, "NO_EXISTE", "obs"));
        }

        @Test
        void actualizarEstado_EnvioInexistente_LanzaExcepcion() {
                when(envioRepository.findById(1)).thenReturn(Optional.empty());

                assertThrows(EnvioException.class,
                                () -> envioService.updatedState(1, "EN_TRANSITO", "obs"));
        }

        @Test
        void actualizarEstado_TransicionValida_ActualizaYRegistraBitacora() {
                Envio envio = new Envio();
                envio.setCodigoRastreo("EXP-1234");
                envio.setEstadoEnvio("PENDIENTE");

                Usuario usuario = new Usuario();
                usuario.setUsername("jdoe");

                Authentication authentication = new UsernamePasswordAuthenticationToken("jdoe", null);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                when(envioRepository.findById(1)).thenReturn(Optional.of(envio));
                when(usuarioRepository.findByUsername("jdoe")).thenReturn(Optional.of(usuario));

                Envio resultado = envioService.updatedState(1, "EN_TRANSITO", "En camino");

                assertEquals("EN_TRANSITO", resultado.getEstadoEnvio());
                verify(bitacoraRepository).save(any(BitacoraEnvio.class));

                SecurityContextHolder.clearContext();
        }

        @Test
        void findBitacora_EnvioExistente_RetornaListaDeBitacoras() {
                BitacoraEnvio registro = new BitacoraEnvio();
                registro.setId(1);
                registro.setEstadoAnterior("PENDIENTE");
                registro.setEstadoNuevo("EN_TRANSITO");
                registro.setFechaCambio(LocalDateTime.now());
                Usuario usuario = new Usuario();
                usuario.setUsername("jdoe");
                registro.setUsuario(usuario);

                when(envioRepository.existsById(1)).thenReturn(true);
                when(bitacoraRepository.findByEnvioIdOrderByFechaCambioDesc(1))
                                .thenReturn(List.of(registro));

                List<BitacoraResponseDTO> resultado = envioService.findBitacora(1);

                assertEquals(1, resultado.size());
        }

        @Test
        void findBitacora_EnvioInexistente_LanzaExcepcion() {
                when(envioRepository.existsById(99)).thenReturn(false);

                assertThrows(ResourceNotFoundException.class,
                                () -> envioService.findBitacora(99));
        }

        @Test
        void updateStatePerVehicle_LlamaAlRepositorio() {
                when(envioRepository.updateEstadoByVehiculoId(1, "CANCELADO")).thenReturn(3);

                int actualizados = envioService.updateStatePerVehicle(1, "CANCELADO");

                assertEquals(3, actualizados);
        }

        @Test
        void delete_LlamaAlRepositorio() {
                envioService.delete(1);

                verify(envioRepository).deleteById(1);
        }

        @Test
        void calcularTarifa_PesoODistanciaInvalidos_LanzaExcepcion() {
                assertThrows(IllegalArgumentException.class,
                                () -> envioService.calcularTarifa(0, 10));
                assertThrows(IllegalArgumentException.class,
                                () -> envioService.calcularTarifa(10, 0));
        }

        @Test
        void calcularTarifa_PesoLigero_RetornaTarifaCorrectaPorDistancia() {
                assertEquals(2500.0, envioService.calcularTarifa(5, 10));
                assertEquals(4000.0, envioService.calcularTarifa(5, 40));
                assertEquals(6000.0, envioService.calcularTarifa(5, 60));
        }

        @Test
        void calcularTarifa_PesoMediano_RetornaTarifaCorrectaPorDistancia() {
                assertEquals(4500.0, envioService.calcularTarifa(20, 10));
                assertEquals(7500.0, envioService.calcularTarifa(20, 40));
                assertEquals(9500.0, envioService.calcularTarifa(20, 60));
        }

        @Test
        void calcularTarifa_PesoPesado_RetornaTarifaCorrectaPorDistancia() {
                assertEquals(12000.0, envioService.calcularTarifa(35, 10));
                assertEquals(15000.0, envioService.calcularTarifa(35, 40));
                assertEquals(20000.0, envioService.calcularTarifa(35, 60));
        }
}