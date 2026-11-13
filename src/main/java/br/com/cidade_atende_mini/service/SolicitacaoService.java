
package br.com.cidade_atende_mini.service;

import br.com.cidade_atende_mini.dto.SolicitacaoDTO;

import org.springframework.stereotype.Service;

@Service
public class SolicitacaoService {



    public void cadastrarSolicitacao(SolicitacaoDTO solicitacaoDTO) {

        System.out.println("Processando nova solicitação...");

        System.out.println("Categoria: " + solicitacaoDTO.getCategoria());
        System.out.println("Bairro: " + solicitacaoDTO.getBairro());
        System.out.println("Descrição: " + solicitacaoDTO.getDescricao());
        System.out.println("Prioridade: " + solicitacaoDTO.getPrioridade());

    }




}
