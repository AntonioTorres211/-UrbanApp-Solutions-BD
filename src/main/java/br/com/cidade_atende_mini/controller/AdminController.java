package br.com.cidade_atende_mini.controller;

import br.com.cidade_atende_mini.model.UsuarioRole;
import br.com.cidade_atende_mini.service.SolicitacaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adm")
public class AdminController {

    private final SolicitacaoService solicitacaoService;

    public AdminController(SolicitacaoService solicitacaoService) {
        this.solicitacaoService = solicitacaoService;
    }

    @GetMapping
    public String painel(Model model, HttpServletRequest request) {
        HttpSession session = sessaoAdmin(request);
        if (session == null) {
            return "redirect:/";
        }

        model.addAttribute("nomeUsuario", session.getAttribute("authenticatedName"));
        model.addAttribute("solicitacoesAbertas", solicitacaoService.listarSolicitacoesAbertas());
        model.addAttribute("solicitacoesFechadas", solicitacaoService.listarSolicitacoesFechadas());
        return "adm";
    }

    @PostMapping("/solicitacoes/{id}/fechar")
    public String fecharSolicitacao(@PathVariable Integer id, HttpServletRequest request) {
        if (sessaoAdmin(request) == null) {
            return "redirect:/";
        }

        solicitacaoService.fecharSolicitacao(id);
        return "redirect:/adm";
    }

    @PostMapping("/solicitacoes/{id}/excluir")
    public String excluirSolicitacao(@PathVariable Integer id, HttpServletRequest request) {
        if (sessaoAdmin(request) == null) {
            return "redirect:/";
        }

        solicitacaoService.excluirSolicitacao(id);
        return "redirect:/adm";
    }

    private HttpSession sessaoAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || !UsuarioRole.ADMIN.name().equals(session.getAttribute("authenticatedRole"))) {
            return null;
        }
        return session;
    }
}
