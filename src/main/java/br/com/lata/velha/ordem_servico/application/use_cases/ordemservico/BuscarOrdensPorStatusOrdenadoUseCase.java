package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.view.OrdemServicoProjection;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class BuscarOrdensPorStatusOrdenadoUseCase {

    private final BuscarOrdensPorStatusOrdenadoGateway gateway;
    private final Logger logger;

    public BuscarOrdensPorStatusOrdenadoUseCase(BuscarOrdensPorStatusOrdenadoGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public PaginatedResult<OrdemServicoProjection> execute(int page, int size) {
        logger.logInfo("Buscando ordens de serviço ordenadas por prioridade de status - page={}, size={}", page, size);
        return gateway.findOrderedByStatusPriority(page, size);
    }
}
