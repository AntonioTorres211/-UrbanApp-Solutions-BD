package br.com.cidade_atende_mini.controller;

import br.com.cidade_atende_mini.dto.AuthResponse;
import br.com.cidade_atende_mini.service.EmailAlreadyRegisteredException;
import br.com.cidade_atende_mini.service.InvalidCredentialsException;
import br.com.cidade_atende_mini.service.InvalidRegistrationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthExceptionHandler {

    @ExceptionHandler(InvalidRegistrationException.class)
    public ResponseEntity<AuthResponse> invalidRegistration(InvalidRegistrationException exception) {
        return error(HttpStatus.BAD_REQUEST,
                "Nome, e-mail ou senha inválidos. Confira os campos e os requisitos da senha.");
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<AuthResponse> invalidRequest(Exception exception, HttpServletRequest request) {
        String message = request.getRequestURI().endsWith("/register")
                ? "Nome, e-mail ou senha inválidos. Confira os campos e os requisitos da senha."
                : "E-mail ou senha inválidos.";
        return error(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<AuthResponse> invalidCredentials(InvalidCredentialsException exception) {
        return error(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos.");
    }

    @ExceptionHandler({EmailAlreadyRegisteredException.class, DataIntegrityViolationException.class})
    public ResponseEntity<AuthResponse> emailAlreadyRegistered(Exception exception) {
        return error(HttpStatus.CONFLICT, "Este e-mail já está cadastrado.");
    }

    private ResponseEntity<AuthResponse> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new AuthResponse(message, false, null, null, null));
    }
}
