package br.com.lata.velha.ordem_servico.application.presenters.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public interface BuscarOrdensPorStatusPresenter {
    PaginatedResult<OrdemServicoResponse> present(PaginatedResult<OrdemServicoProjection> projections);
}
