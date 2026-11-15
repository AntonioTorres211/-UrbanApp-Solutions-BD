
package br.com.cidade_atende_mini.controller;

import br.com.cidade_atende_mini.dto.SolicitacaoDTO;
import br.com.cidade_atende_mini.service.SolicitacaoService;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    // Exibe o formulário e lista as solicitações
    @GetMapping("/solicitacoes/nova")
    public String novaSolicitacao(Model model) {
        model.addAttribute(
            "solicitacoes",
            solicitacaoService.listarSolicitacoes()
        );

        return "index";
    }

    // Recebe e cadastra uma nova solicitação
    @PostMapping("/solicitacoes")
    public String cadastrarSolicitacao(
            @ModelAttribute SolicitacaoDTO solicitacaoDTO) {

        solicitacaoService.cadastrarSolicitacao(solicitacaoDTO);

        return "redirect:/solicitacoes/nova";
    }
}
