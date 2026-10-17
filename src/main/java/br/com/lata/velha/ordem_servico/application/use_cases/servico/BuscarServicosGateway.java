package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public interface BuscarServicosGateway {
    PaginatedResult<Servico> findAll(int page, int size);
}
