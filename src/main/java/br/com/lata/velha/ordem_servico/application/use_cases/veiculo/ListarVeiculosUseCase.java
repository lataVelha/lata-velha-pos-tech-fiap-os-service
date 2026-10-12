package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class ListarVeiculosUseCase {

    private final ListarVeiculosGateway gateway;
    private final Logger logger;

    public ListarVeiculosUseCase(ListarVeiculosGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public PaginatedResult<Veiculo> execute(int page, int size) {
        logger.logInfo("Listando veículos - page={}, size={}", page, size);
        return gateway.findAll(page, size);
    }
}
