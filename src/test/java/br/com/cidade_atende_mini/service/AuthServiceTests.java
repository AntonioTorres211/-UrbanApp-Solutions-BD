package br.com.cidade_atende_mini.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.cidade_atende_mini.model.Usuario;
import br.com.cidade_atende_mini.model.UsuarioRole;
import br.com.cidade_atende_mini.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:auth-tests;DB_CLOSE_DELAY=-1")
class AuthServiceTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void limparUsuarios() {
        usuarioRepository.deleteAll();
    }

    @Test
    void cadastraEmailNormalizadoESalvaSenhaComHash() {
        authService.register(" Pessoa Exemplo ", "Pessoa@Exemplo.com", "SenhaForte1!");

        Usuario usuario = usuarioRepository.findByEmail("pessoa@exemplo.com").orElseThrow();
        assertEquals("Pessoa Exemplo", usuario.getNome());
        assertEquals(UsuarioRole.USER, usuario.getRole());
        assertNotEquals("SenhaForte1!", usuario.getSenhaHash());
        assertTrue(passwordEncoder.matches("SenhaForte1!", usuario.getSenhaHash()));
    }

    @Test
    void criaAdministradorInicialComSenhaHashEPermiteReexecutarBootstrap() {
        authService.createInitialAdmin("admin@urbanapp.local", "AdminLocal1#");
        authService.createInitialAdmin("admin@urbanapp.local", "AdminLocal1#");

        Usuario admin = usuarioRepository.findByEmail("admin@urbanapp.local").orElseThrow();
        assertEquals("SuperUser", admin.getNome());
        assertEquals(UsuarioRole.ADMIN, admin.getRole());
        assertTrue(passwordEncoder.matches("AdminLocal1#", admin.getSenhaHash()));
        assertEquals(1, usuarioRepository.count());
    }

    @Test
    void rejeitaSenhasQueNaoAtendemPoliticaOuSaoComuns() {
        for (String senha : new String[] {
                "Aa1!", "abcdefgh1!", "ABCDEFGH1!", "Abcdefgh1",
                "Abc def1!", "Password1!"
        }) {
            assertThrows(InvalidRegistrationException.class,
                    () -> authService.register("Pessoa Exemplo", "pessoa@exemplo.com", senha), senha);
        }
        assertEquals(0, usuarioRepository.count());
    }

    @Test
    void rejeitaEmailDuplicadoSemDiferenciarMaiusculas() {
        authService.register("Pessoa Exemplo", "pessoa@exemplo.com", "SenhaForte1!");

        assertThrows(EmailAlreadyRegisteredException.class,
                () -> authService.register("Outra Pessoa", "PESSOA@EXEMPLO.COM", "OutraSenha2@"));
    }

    @Test
    void autenticaComEmailNormalizadoERejeitaSenhaErrada() {
        authService.register("Pessoa Exemplo", "pessoa@exemplo.com", "SenhaForte1!");

        Usuario usuario = authService.authenticate("PESSOA@EXEMPLO.COM", "SenhaForte1!");
        assertEquals("pessoa@exemplo.com", usuario.getEmail());
        assertThrows(InvalidCredentialsException.class,
                () -> authService.authenticate("pessoa@exemplo.com", "SenhaErrada1!"));
    }
}
