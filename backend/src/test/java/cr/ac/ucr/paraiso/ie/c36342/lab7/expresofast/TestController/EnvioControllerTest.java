package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.TestController;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.security.JwtTokenProvider;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.EnvioService;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.controller.EnvioController;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Conductor;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.EmpresaLogistica;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Envio;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Vehiculo;

@WebMvcTest(controllers = EnvioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EnvioControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private EnvioService envioService;
        @MockitoBean
        private JwtTokenProvider jwtTokenProvider;
        @MockitoBean
        private UserDetailsService userDetailsService;

        @Test
        void getEnvio_Existente_RetornaHttp200YAtributosJson() throws Exception {
                EmpresaLogistica empresa = new EmpresaLogistica();
                empresa.setId(1);
                empresa.setNombre("ExpresoFast Transportes");

                Vehiculo vehiculo = new Vehiculo();
                vehiculo.setId(1);
                vehiculo.setEmpresa(empresa);

                Conductor conductor = new Conductor();
                conductor.setId(2);

                Envio envio = new Envio();
                envio.setId(10);
                envio.setCodigoRastreo("EXP-1234");
                envio.setDireccionDestino("San Jose");
                envio.setPesoKg(new BigDecimal("10"));
                envio.setCosto(new BigDecimal("2500"));
                envio.setEstadoEnvio("PENDIENTE");
                envio.setVehiculo(vehiculo);
                envio.setConductor(conductor);

                when(envioService.findById(10))
                                .thenReturn(Optional.of(envio));

                mockMvc.perform(get("/api/envios/{id}", 10))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.codigoRastreo").value("EXP-1234"))
                                .andExpect(jsonPath("$.estadoEnvio").value("PENDIENTE"))
                                .andExpect(jsonPath("$.empresaNombre").value("ExpresoFast Transportes"));
        }

        @Test
        void getEnvio_NoExistente_RetornaHttp404() throws Exception {
                when(envioService.findById(anyInt()))
                                .thenReturn(Optional.empty());

                mockMvc.perform(get("/api/envios/{id}", 999))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.detail").exists())
                                .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        void crearEnvio_PayloadInvalido_RetornaHttp400ConErroresDeValidacion() throws Exception {
                String payloadInvalido = """
                                {
                                  "codigoRastreo": "",
                                  "direccionDestino": "",
                                  "pesoKg": -5,
                                  "costo": null,
                                  "vehiculoId": null,
                                  "conductorId": null
                                }
                                """;

                mockMvc.perform(post("/api/envios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payloadInvalido))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.error").value("Datos inválidos"))
                                .andExpect(jsonPath("$.fields.codigoRastreo").exists())
                                .andExpect(jsonPath("$.fields.pesoKg").exists())
                                .andExpect(jsonPath("$.fields.vehiculoId").exists())
                                .andExpect(jsonPath("$.fields.conductorId").exists());
        }
}
