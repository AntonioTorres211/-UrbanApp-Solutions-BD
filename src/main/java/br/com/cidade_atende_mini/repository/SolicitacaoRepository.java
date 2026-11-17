
package br.com.cidade_atende_mini.repository;

import br.com.cidade_atende_mini.model.Solicitacao;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Integer> {

    List<Solicitacao> findByStatusIgnoreCase(String status);

    List<Solicitacao> findByStatusIn(List<String> statuses);

    List<Solicitacao> findByUsuario_IdOrderByDataAberturaDesc(Long usuarioId);
}
