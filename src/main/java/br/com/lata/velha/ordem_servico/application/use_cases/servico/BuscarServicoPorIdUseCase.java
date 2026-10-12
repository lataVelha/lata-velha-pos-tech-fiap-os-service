package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.shared.application.logging.Logger;

public class BuscarServicoPorIdUseCase {

    private final BuscarServicoPorIdGateway gateway;
    private final Logger logger;

    public BuscarServicoPorIdUseCase(BuscarServicoPorIdGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public Servico execute(Long id) {
        logger.logInfo("Buscando serviço por id - servicoId={}", id);
        return gateway.getServicoPorId(id);
    }
}
