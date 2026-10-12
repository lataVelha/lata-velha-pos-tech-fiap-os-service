package br.com.lata.velha.ordem_servico.api.presenters.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.application.presenters.veiculo.ListarVeiculosPresenter;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import org.springframework.stereotype.Component;

@Component
public class ListarVeiculosPresenterImpl implements ListarVeiculosPresenter {
    @Override
    public PaginatedResult<VeiculoResponse> present(PaginatedResult<Veiculo> veiculos) {
        return PaginatedResult.map(veiculos, VeiculoResponse::from);
    }
}
