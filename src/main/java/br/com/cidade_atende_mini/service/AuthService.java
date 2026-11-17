package br.com.cidade_atende_mini.service;

import br.com.cidade_atende_mini.model.Usuario;
import br.com.cidade_atende_mini.model.UsuarioRole;
import br.com.cidade_atende_mini.repository.UsuarioRepository;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Set<String> COMMON_PASSWORDS = Stream.of(
            "12345678", "123456789", "1234567890", "password", "password1",
            "password123", "passw0rd", "p@ssw0rd", "p@ssword", "qwerty",
            "qwerty123", "qwertyuiop", "abc12345", "iloveyou", "letmein",
            "welcome", "admin", "admin123", "senha123", "senha1234",
            "brasil123", "mudar123", "123mudar!")
            .map(AuthService::normalizePassword)
            .collect(Collectors.toUnmodifiableSet());

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String name, String email, String password) {
        createAccount(name, email, password, UsuarioRole.USER);
    }

    public void createInitialAdmin(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        Optional<Usuario> existingUser = usuarioRepository.findByEmail(normalizedEmail);
        if (existingUser.isPresent()) {
            if (existingUser.get().getRole() == UsuarioRole.ADMIN) {
                return;
            }
            throw new IllegalStateException(
                    "O e-mail configurado para o administrador já pertence a uma conta comum.");
        }

        if (!isPasswordAllowed(password)) {
            throw new IllegalStateException(
                    "ADMIN_PASSWORD não atende aos requisitos de senha do cadastro.");
        }

        createAccount("SuperUser", normalizedEmail, password, UsuarioRole.ADMIN);
    }

    private void createAccount(String name, String email, String password, UsuarioRole role) {
        if (!isPasswordAllowed(password)) {
            throw new InvalidRegistrationException();
        }

        String normalizedName = name.strip();
        if (normalizedName.isEmpty()) {
            throw new InvalidRegistrationException();
        }

        String normalizedEmail = normalizeEmail(email);
        if (usuarioRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyRegisteredException();
        }

        Usuario usuario = new Usuario();
        usuario.setNome(normalizedName);
        usuario.setEmail(normalizedEmail);
        usuario.setSenhaHash(passwordEncoder.encode(password));
        usuario.setRole(role);
        usuarioRepository.save(usuario);
    }

    public Usuario authenticate(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        Usuario usuario = usuarioRepository.findByEmail(normalizedEmail)
                .filter(conta -> passwordEncoder.matches(password, conta.getSenhaHash()))
                .orElseThrow(InvalidCredentialsException::new);
        return usuario;
    }

    private boolean isPasswordAllowed(String password) {
        int characterCount = password.codePointCount(0, password.length());
        if (characterCount < 8) {
            return false;
        }

        boolean hasWhitespace = password.codePoints()
                .anyMatch(codePoint -> Character.isWhitespace(codePoint) || Character.isSpaceChar(codePoint));
        boolean hasUppercase = password.codePoints().anyMatch(Character::isUpperCase);
        boolean hasLowercase = password.codePoints().anyMatch(Character::isLowerCase);
        boolean hasSpecialCharacter = password.codePoints().anyMatch(codePoint ->
                !Character.isLetterOrDigit(codePoint)
                        && !Character.isWhitespace(codePoint)
                        && !Character.isSpaceChar(codePoint));

        return !hasWhitespace
                && hasUppercase
                && hasLowercase
                && hasSpecialCharacter
                && !COMMON_PASSWORDS.contains(normalizePassword(password));
    }

    private static String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    private static String normalizePassword(String password) {
        return password.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}
