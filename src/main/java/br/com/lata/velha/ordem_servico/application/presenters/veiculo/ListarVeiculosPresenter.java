package br.com.lata.velha.ordem_servico.application.presenters.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public interface ListarVeiculosPresenter {
    PaginatedResult<VeiculoResponse> present(PaginatedResult<Veiculo> veiculos);
}
