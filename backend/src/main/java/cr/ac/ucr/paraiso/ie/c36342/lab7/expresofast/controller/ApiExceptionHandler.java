package cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import java.util.stream.Collectors;

import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.EnvioException;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.ResourceNotFoundException;
import cr.ac.ucr.paraiso.ie.c36342.lab7.expresofast.business.InvalidStateTransitionException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EnvioException.class)
    public ResponseEntity<Map<String, String>> handleEnvioException(EnvioException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        var errors = exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(error -> error.getField(), error -> error.getDefaultMessage(), (first, second) -> first));
        return ResponseEntity.badRequest().body(Map.of("error", "Datos inválidos", "fields", errors));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problem.setTitle("Recurso no encontrado");
        return problem;
    }

    @ExceptionHandler(InvalidStateTransitionException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTransition(InvalidStateTransitionException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales inválidas"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No tiene permisos para esta operación"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpectedException(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Ocurrió un error interno"));
    }
}