package br.com.cidade_atende_mini.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.cidade_atende_mini.dto.SolicitacaoDTO;
import br.com.cidade_atende_mini.model.Solicitacao;
import br.com.cidade_atende_mini.model.Usuario;
import br.com.cidade_atende_mini.model.UsuarioRole;
import br.com.cidade_atende_mini.repository.SolicitacaoRepository;
import br.com.cidade_atende_mini.repository.UsuarioRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:admin-solicitacoes-tests;DB_CLOSE_DELAY=-1")
class SolicitacaoAdminServiceTests {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @Autowired
    private SolicitacaoRepository solicitacaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void limparSolicitacoes() {
        solicitacaoRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    void listaSomenteSolicitacoesAbertas() {
        salvarSolicitacao("ABERTA");
        salvarSolicitacao("FECHADA");

        List<Solicitacao> abertas = solicitacaoService.listarSolicitacoesAbertas();

        assertEquals(1, abertas.size());
        assertEquals("ABERTA", abertas.get(0).getStatus());
    }

    @Test
    void listaSolicitacoesFechadasEConcluidas() {
        salvarSolicitacao("ABERTA");
        salvarSolicitacao("FECHADA");
        salvarSolicitacao("CONCLUIDA");

        List<Solicitacao> fechadas = solicitacaoService.listarSolicitacoesFechadas();

        assertEquals(2, fechadas.size());
    }

    @Test
    void fechaSolicitacaoAbertaERegistraData() {
        Solicitacao solicitacao = salvarSolicitacao("ABERTA");

        solicitacaoService.fecharSolicitacao(solicitacao.getId());

        Solicitacao fechada = solicitacaoRepository.findById(solicitacao.getId()).orElseThrow();
        assertEquals("FECHADA", fechada.getStatus());
        assertNotNull(fechada.getDataFechadura());
    }

    @Test
    void excluiSolicitacaoExistenteERejeitaIdInexistente() {
        Solicitacao solicitacao = salvarSolicitacao("ABERTA");
        solicitacaoService.excluirSolicitacao(solicitacao.getId());

        assertEquals(0, solicitacaoRepository.count());
        assertThrows(ResponseStatusException.class,
                () -> solicitacaoService.excluirSolicitacao(solicitacao.getId()));
    }

    @Test
    void cadastraESoListaSolicitacoesDoUsuarioAutenticado() {
        Usuario autor = salvarUsuario();
        Usuario outroUsuario = salvarUsuario();
        SolicitacaoDTO solicitacaoDTO = new SolicitacaoDTO();
        solicitacaoDTO.setCategoria("Iluminação");
        solicitacaoDTO.setBairro("Centro");
        solicitacaoDTO.setDescricao("Lâmpada queimada");
        solicitacaoDTO.setPrioridade(3);

        solicitacaoService.cadastrarSolicitacao(solicitacaoDTO, autor.getId());

        List<Solicitacao> solicitacoesDoAutor =
                solicitacaoService.listarSolicitacoesDoUsuario(autor.getId());
        List<Solicitacao> solicitacoesDoOutroUsuario =
                solicitacaoService.listarSolicitacoesDoUsuario(outroUsuario.getId());

        assertEquals(1, solicitacoesDoAutor.size());
        assertEquals(autor.getId(), solicitacoesDoAutor.get(0).getUsuario().getId());
        assertEquals(0, solicitacoesDoOutroUsuario.size());
    }

    private Usuario salvarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Usuário de teste");
        usuario.setEmail(UUID.randomUUID() + "@teste.local");
        usuario.setSenhaHash("hash-de-teste");
        usuario.setRole(UsuarioRole.USER);
        return usuarioRepository.save(usuario);
    }

    private Solicitacao salvarSolicitacao(String status) {
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setTitulo("Iluminação");
        solicitacao.setDescricao("Lâmpada queimada");
        solicitacao.setEndereco("Centro");
        solicitacao.setPrioridade(3);
        solicitacao.setStatus(status);
        return solicitacaoRepository.save(solicitacao);
    }
}
