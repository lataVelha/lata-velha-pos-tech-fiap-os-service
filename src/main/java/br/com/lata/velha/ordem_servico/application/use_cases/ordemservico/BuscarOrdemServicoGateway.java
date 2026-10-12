package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public interface BuscarOrdemServicoGateway {
    PaginatedResult<OrdemServicoProjection> findByFiltros(Long id, String status, Long proprietarioId, Long mecanicoId, int page, int size);
}
