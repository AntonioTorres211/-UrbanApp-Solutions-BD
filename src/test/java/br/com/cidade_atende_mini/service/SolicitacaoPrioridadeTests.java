package br.com.cidade_atende_mini.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.cidade_atende_mini.dto.SolicitacaoDTO;
import br.com.cidade_atende_mini.model.Usuario;
import br.com.cidade_atende_mini.model.UsuarioRole;
import br.com.cidade_atende_mini.repository.SolicitacaoRepository;
import br.com.cidade_atende_mini.repository.UsuarioRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

/**
 * RF02 - Aceitar prioridade apenas como número inteiro de 1 a 5.
 *
 * Fase VERMELHA do TDD: os testes de prioridade inválida devem falhar
 * enquanto a regra não existir no servidor.
 */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:rf02-prioridade-tests;DB_CLOSE_DELAY=-1")
class SolicitacaoPrioridadeTests {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario autor;

    @BeforeEach
    void prepararCenario() {
        solicitacaoRepository.deleteAll();
        usuarioRepository.deleteAll();
        autor = salvarUsuario();
    }

    // ---------- Prioridades válidas ----------

    @Test
    void aceitaPrioridadeDentroDoIntervalo() {
        solicitacaoService.cadastrarSolicitacao(solicitacaoComPrioridade(3), autor.getId());

        assertEquals(1, solicitacaoRepository.count());
    }

    @Test
    void aceitaLimiteInferior() {
        solicitacaoService.cadastrarSolicitacao(solicitacaoComPrioridade(1), autor.getId());

        assertEquals(1, solicitacaoRepository.count());
    }

    @Test
    void aceitaLimiteSuperior() {
        solicitacaoService.cadastrarSolicitacao(solicitacaoComPrioridade(5), autor.getId());

        assertEquals(1, solicitacaoRepository.count());
    }

    // ---------- Prioridades inválidas ----------

    @Test
    void rejeitaPrioridadeAbaixoDoLimite() {
        assertRejeitaComErro400(0);
    }

    @Test
    void rejeitaPrioridadeAcimaDoLimite() {
        assertRejeitaComErro400(6);
    }

    @Test
    void rejeitaPrioridadeNegativa() {
        assertRejeitaComErro400(-1);
    }

    @Test
    void rejeitaPrioridadeNula() {
        assertRejeitaComErro400(null);
    }

    // ---------- Apoio ----------

    private void assertRejeitaComErro400(Integer prioridade) {
        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> solicitacaoService.cadastrarSolicitacao(
                        solicitacaoComPrioridade(prioridade), autor.getId()));

        assertEquals(401, erro.getStatusCode().value());
        assertEquals(0, solicitacaoRepository.count(),
                "Uma solicitação com prioridade inválida não pode ser salva.");
    }

    private SolicitacaoDTO solicitacaoComPrioridade(Integer prioridade) {
        SolicitacaoDTO dto = new SolicitacaoDTO();
        dto.setCategoria("Iluminação");
        dto.setBairro("Centro");
        dto.setDescricao("Lâmpada queimada");
        dto.setPrioridade(prioridade);
        return dto;
    }

    private Usuario salvarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmail(UUID.randomUUID() + "@teste.local");
        usuario.setSenhaHash("hash-de-teste");
        usuario.setRole(UsuarioRole.USER);
        return usuarioRepository.save(usuario);
    }
}