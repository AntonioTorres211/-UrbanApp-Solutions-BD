
package br.com.cidade_atende_mini.controller;

import br.com.cidade_atende_mini.dto.SolicitacaoDTO;
import br.com.cidade_atende_mini.service.SolicitacaoService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @PostMapping("/solicitacoes")
    public String cadastrarSolicitacao(
            @ModelAttribute SolicitacaoDTO solicitacaoDTO) {

        solicitacaoService.cadastrarSolicitacao(solicitacaoDTO);

        return "redirect:/";
    }
}
