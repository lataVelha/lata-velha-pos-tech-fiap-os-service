package br.com.lata.velha.ordem_servico.application.presenters.servico;

import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public interface BuscarServicosPresenter {
    PaginatedResult<ServicoResponse> present(PaginatedResult<Servico> servicos);
}
