package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.TestServices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.AuthService;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Rol;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Usuario;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.dto.AuthRequestDTO;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.dto.AuthResponseDTO;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthServiceServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_CredencialesValidas_RetornaAuthResponseDTO() {
        Rol rolAdmin = new Rol();
        rolAdmin.setNombreRol("ADMIN");

        Usuario usuario = new Usuario();
        usuario.setUsername("jdoe");
        usuario.getRoles().add(rolAdmin);

        AuthRequestDTO request = new AuthRequestDTO("jdoe", "clave123");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(tokenProvider.generateToken(authentication)).thenReturn("token-generado");
        when(tokenProvider.getExpirationMs()).thenReturn(3600000L);

        AuthResponseDTO respuesta = authService.login(request);

        assertEquals("token-generado", respuesta.token());
        assertEquals("jdoe", respuesta.username());
        assertEquals(Set.of("ADMIN"), respuesta.roles());
        assertEquals(3600000L, respuesta.expirationTime());
    }
}