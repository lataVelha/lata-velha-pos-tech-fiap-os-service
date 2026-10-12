package br.com.lata.velha.authentication.api.handlers;

import br.com.lata.velha.authentication.domain.exceptions.InactiveUserException;
import br.com.lata.velha.authentication.domain.exceptions.InvalidLoginException;
import br.com.lata.velha.shared.application.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

/**
 * Exceções específicas do contexto de autenticação — ficam aqui (e não no
 * GlobalExceptionHandler do shared kernel) porque o Inventory não tem essas classes.
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class AuthExceptionHandler {

    private final Logger logger;

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidLogin(InvalidLoginException ex) {
        logger.logWarn("Requisição rejeitada - status=401 UNAUTHORIZED, motivo={}", ex.getMessage());
        return build(HttpStatus.UNAUTHORIZED, "Unauthorized", ex.getMessage());
    }

    @ExceptionHandler(InactiveUserException.class)
    public ResponseEntity<Map<String, Object>> handleInactiveUser(InactiveUserException ex) {
        logger.logWarn("Requisição rejeitada - status=422 UNPROCESSABLE_ENTITY, motivo={}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(
                Map.of("error", error, "message", message, "timestamp", Instant.now().toString()));
    }
}
