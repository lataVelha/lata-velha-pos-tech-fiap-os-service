package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;

public class BuscarServicosUseCase {

    private final BuscarServicosGateway gateway;
    private final Logger logger;

    public BuscarServicosUseCase(BuscarServicosGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public PaginatedResult<Servico> execute(int page, int size) {
        logger.logInfo("Buscando serviços - page={}, size={}", page, size);
        return gateway.findAll(page, size);
    }
}
