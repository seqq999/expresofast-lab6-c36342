package cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.controller;

import org.springframework.web.bind.annotation.*;

import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.business.AuthService;
import cr.ac.ucr.paraiso.ie.c36342.lab5.expresofast.dto.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/login")
    public AuthResponseDTO login(@Valid @RequestBody AuthRequestDTO request) { return authService.login(request); }
}