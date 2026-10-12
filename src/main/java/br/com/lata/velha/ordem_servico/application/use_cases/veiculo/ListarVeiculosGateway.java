package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public interface ListarVeiculosGateway {
    PaginatedResult<Veiculo> findAll(int page, int size);
}
