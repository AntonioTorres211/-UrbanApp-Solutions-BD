package br.com.cidade_atende_mini.controller;

import java.net.URI;
import br.com.cidade_atende_mini.dto.AuthRequest;
import br.com.cidade_atende_mini.dto.AuthResponse;
import br.com.cidade_atende_mini.dto.RegistrationRequest;
import br.com.cidade_atende_mini.model.Usuario;
import br.com.cidade_atende_mini.model.UsuarioRole;
import br.com.cidade_atende_mini.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationRequest request) {
        authService.register(request.name(), request.email(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse("Usuário cadastrado com sucesso.", false, null, null, null));
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody AuthRequest authRequest,
            HttpServletRequest request) {
        Usuario usuario = authService.authenticate(authRequest.email(), authRequest.password());
        HttpSession session = request.getSession(true);
        request.changeSessionId();
        session.setAttribute("authenticatedUserId", usuario.getId());
        session.setAttribute("authenticatedEmail", usuario.getEmail());
        UsuarioRole role = usuario.getRole() == null ? UsuarioRole.USER : usuario.getRole();
        String name = displayName(usuario);
        session.setAttribute("authenticatedName", name);
        session.setAttribute("authenticatedRole", role.name());

        return new AuthResponse("Login realizado com sucesso.", true, usuario.getEmail(), name, role.name());
    }

    @GetMapping("/session")
    public AuthResponse session(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !(session.getAttribute("authenticatedEmail") instanceof String email)) {
            return new AuthResponse("", false, null, null, null);
        }

        String name = session.getAttribute("authenticatedName") instanceof String authenticatedName
                ? authenticatedName
                : email.substring(0, email.indexOf('@'));
        String role = session.getAttribute("authenticatedRole") instanceof String authenticatedRole
                ? authenticatedRole
                : UsuarioRole.USER.name();
        return new AuthResponse("", true, email, name, role);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.status(HttpStatus.SEE_OTHER)
                .location(URI.create("/"))
                .build();
    }

    private String displayName(Usuario usuario) {
        if (usuario.getNome() != null && !usuario.getNome().isBlank()) {
            return usuario.getNome();
        }
        return usuario.getEmail().substring(0, usuario.getEmail().indexOf('@'));
    }
}
