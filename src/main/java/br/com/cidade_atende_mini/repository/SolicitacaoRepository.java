
package br.com.cidade_atende_mini.repository;

import br.com.cidade_atende_mini.model.Solicitacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Integer> {


}
