package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

import java.util.Set;

public interface ServicoRepository {

    Servico save(Servico servico);

    Servico getActiveById(Long id);

    PaginatedResult<Servico> findAllActivePaginated(int page, int size);

    Set<Servico> getAllActiveById(Set<Long> servicoIds);
}
