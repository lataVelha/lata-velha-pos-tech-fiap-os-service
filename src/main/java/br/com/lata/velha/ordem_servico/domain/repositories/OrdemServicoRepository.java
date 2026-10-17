package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public interface OrdemServicoRepository {

    OrdemServico save(OrdemServico ordemServico);

    OrdemServico getById(Long id);

    OrdemServico getByIdWithExecucoesAndPecas(Long id);

    PaginatedResult<OrdemServicoProjection> findByAllOrdemSevico(Long id,
                                                                 String status,
                                                                 Long proprietarioId,
                                                                 Long mecanicoId,
                                                                 int page,
                                                                 int size);

    PaginatedResult<OrdemServicoProjection> findOrderedByStatusPriority(int page, int size);
}
