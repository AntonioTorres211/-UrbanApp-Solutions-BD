
package br.com.cidade_atende_mini.service;

import br.com.cidade_atende_mini.dto.SolicitacaoDTO;
import br.com.cidade_atende_mini.model.Solicitacao;
import br.com.cidade_atende_mini.repository.SolicitacaoRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;

    public SolicitacaoService(SolicitacaoRepository solicitacaoRepository) {
        this.solicitacaoRepository = solicitacaoRepository;
    }

    public void cadastrarSolicitacao(SolicitacaoDTO solicitacaoDTO) {

        Solicitacao solicitacao = new Solicitacao();

        // Conversão dos dados do DTO para a entidade
        solicitacao.setTitulo(solicitacaoDTO.getCategoria());
        solicitacao.setEndereco(solicitacaoDTO.getBairro());
        solicitacao.setDescricao(solicitacaoDTO.getDescricao());
        solicitacao.setPrioridade(solicitacaoDTO.getPrioridade());

        // Valores iniciais da solicitação
        solicitacao.setStatus("ABERTA");
        solicitacao.setDataAbertura(LocalDateTime.now());

        // Persistência no banco de dados
        solicitacaoRepository.save(solicitacao);
    }
}
