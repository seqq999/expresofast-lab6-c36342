package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business;

import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.domain.Usuario;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.dto.*;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.security.JwtTokenProvider;

@Service
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        Usuario user = (Usuario) authentication.getPrincipal();
        return new AuthResponseDTO(tokenProvider.generateToken(authentication), user.getUsername(),
                user.getRoles().stream().map(role -> role.getNombreRol()).collect(Collectors.toSet()),
                tokenProvider.getExpirationMs());
    }

}