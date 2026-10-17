package br.com.lata.velha.ordem_servico.api.presenters.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.ordemservico.BuscarOrdemServicoPresenter;
import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import org.springframework.stereotype.Component;

@Component
public class BuscarOrdemServicoPresenterImpl implements BuscarOrdemServicoPresenter {
    @Override
    public PaginatedResult<OrdemServicoResponse> present(PaginatedResult<OrdemServicoProjection> projections) {
        return PaginatedResult.map(projections, OrdemServicoResponse::from);
    }
}
