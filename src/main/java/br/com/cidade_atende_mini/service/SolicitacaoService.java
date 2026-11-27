
package br.com.cidade_atende_mini.service;

import br.com.cidade_atende_mini.dto.SolicitacaoDTO;
import br.com.cidade_atende_mini.model.Solicitacao;
import br.com.cidade_atende_mini.model.Usuario;
import br.com.cidade_atende_mini.repository.SolicitacaoRepository;
import br.com.cidade_atende_mini.repository.UsuarioRepository;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

@Service
public class SolicitacaoService {
    private static final int PRIORIDADE_MINIMA = 1;
    private static final int PRIORIDADE_MAXIMA = 6;

    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public SolicitacaoService(
            SolicitacaoRepository solicitacaoRepository,
            UsuarioRepository usuarioRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void cadastrarSolicitacao(SolicitacaoDTO solicitacaoDTO, Long usuarioId) {

        validarPrioridade(solicitacaoDTO.getPrioridade());

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        Solicitacao solicitacao = new Solicitacao();

        // Conversão dos dados do DTO para a entidade
        solicitacao.setTitulo(solicitacaoDTO.getCategoria());
        solicitacao.setEndereco(solicitacaoDTO.getBairro());
        solicitacao.setDescricao(solicitacaoDTO.getDescricao());
        solicitacao.setPrioridade(solicitacaoDTO.getPrioridade());

        // Valores iniciais da solicitação
        solicitacao.setStatus("ABERTA");
        solicitacao.setDataAbertura(LocalDateTime.now());
        solicitacao.setUsuario(usuario);

        // Persistência no banco de dados
        solicitacaoRepository.save(solicitacao);
    }

    private void validarPrioridade(Integer prioridade) {
        if (!prioridadeValida(prioridade)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A prioridade deve ser um número inteiro de "
                            + PRIORIDADE_MINIMA + " a " + PRIORIDADE_MAXIMA + ".");
        }
    }

    private boolean prioridadeValida(Integer prioridade) {
        return prioridade != null
                && prioridade >= PRIORIDADE_MINIMA
                && prioridade <= PRIORIDADE_MAXIMA;
    }

    public List<Solicitacao> listarSolicitacoesDoUsuario(Long usuarioId) {
        return solicitacaoRepository.findByUsuario_IdOrderByDataAberturaDesc(usuarioId);
    }

    public List<Solicitacao> listarSolicitacoesAbertas() {
        return solicitacaoRepository.findByStatusIgnoreCase("ABERTA");
    }

    public List<Solicitacao> listarSolicitacoesFechadas() {
        return solicitacaoRepository.findByStatusIn(List.of("FECHADA", "CONCLUIDA"));
    }

    public void fecharSolicitacao(Integer id) {
        Solicitacao solicitacao = solicitacaoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!"ABERTA".equalsIgnoreCase(solicitacao.getStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A solicitação não está aberta.");
        }

        solicitacao.setStatus("FECHADA");
        solicitacao.setDataFechadura(LocalDateTime.now());
        solicitacaoRepository.save(solicitacao);
    }

    public void excluirSolicitacao(Integer id) {
        if (!solicitacaoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        solicitacaoRepository.deleteById(id);
    }

}
