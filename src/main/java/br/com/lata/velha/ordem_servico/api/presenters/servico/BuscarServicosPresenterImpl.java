package br.com.lata.velha.ordem_servico.api.presenters.servico;

import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.servico.BuscarServicosPresenter;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import org.springframework.stereotype.Component;

@Component
public class BuscarServicosPresenterImpl implements BuscarServicosPresenter {
    @Override
    public PaginatedResult<ServicoResponse> present(PaginatedResult<Servico> servicos) {
        return PaginatedResult.map(servicos, ServicoResponse::from);
    }
}
