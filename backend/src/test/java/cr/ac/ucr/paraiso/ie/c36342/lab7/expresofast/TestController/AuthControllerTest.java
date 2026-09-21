package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.TestController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.AuthService;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.controller.AuthController;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.dto.AuthResponseDTO;

import org.springframework.security.core.userdetails.UserDetailsService;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.security.JwtTokenProvider;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // deshabilita filtros de seguridad para probar solo el contrato HTTP
public class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AuthService authService;

        @MockitoBean
        private JwtTokenProvider jwtTokenProvider;

        @MockitoBean
        private UserDetailsService userDetailsService;

        @Test
        void login_CredencialesCorrectas_RetornaHttp200ConToken() throws Exception {
                AuthResponseDTO respuesta = new AuthResponseDTO(
                                "eyJhbGciOiJIUzI1NiJ9.fake.token",
                                "jdoe",
                                Set.of("ADMIN"),
                                3600000L);

                when(authService.login(any())).thenReturn(respuesta);

                String requestBody = """
                                {
                                  "username": "jdoe",
                                  "password": "claveCorrecta123"
                                }
                                """;

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.token").value("eyJhbGciOiJIUzI1NiJ9.fake.token"))
                                .andExpect(jsonPath("$.username").value("jdoe"));
        }

        @Test
        void login_CredencialesIncorrectas_RetornaHttp401() throws Exception {
                when(authService.login(any())).thenThrow(new BadCredentialsException("Credenciales inválidas"));

                String requestBody = """
                                {
                                  "username": "jdoe",
                                  "password": "claveIncorrecta"
                                }
                                """;

                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isUnauthorized())
                                .andExpect(jsonPath("$.error").exists());
        }
}