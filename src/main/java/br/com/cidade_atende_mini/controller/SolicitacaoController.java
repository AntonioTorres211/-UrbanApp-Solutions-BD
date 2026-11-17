
package br.com.cidade_atende_mini.controller;

import br.com.cidade_atende_mini.dto.SolicitacaoDTO;
import br.com.cidade_atende_mini.service.SolicitacaoService;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    public SolicitacaoController(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    // Exibe o formulário e lista as solicitações
    @GetMapping("/solicitacoes/nova")
    public String novaSolicitacao(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long usuarioId = usuarioIdAutenticado(session);
        if (usuarioId == null) {
            return "redirect:/";
        }

        model.addAttribute("nomeUsuario", session.getAttribute("authenticatedName"));
        model.addAttribute(
            "solicitacoes",
            solicitacaoService.listarSolicitacoesDoUsuario(usuarioId)
        );

        return "index";
    }

    // Recebe e cadastra uma nova solicitação
    @PostMapping("/solicitacoes")
    public String cadastrarSolicitacao(
            @ModelAttribute SolicitacaoDTO solicitacaoDTO,
            HttpServletRequest request) {
        Long usuarioId = usuarioIdAutenticado(request.getSession(false));
        if (usuarioId == null) {
            return "redirect:/";
        }

        solicitacaoService.cadastrarSolicitacao(solicitacaoDTO, usuarioId);

        return "redirect:/solicitacoes/nova";
    }

    private Long usuarioIdAutenticado(HttpSession session) {
        if (session == null) {
            return null;
        }
        Object usuarioId = session.getAttribute("authenticatedUserId");
        return usuarioId instanceof Long id ? id : null;
    }
}
