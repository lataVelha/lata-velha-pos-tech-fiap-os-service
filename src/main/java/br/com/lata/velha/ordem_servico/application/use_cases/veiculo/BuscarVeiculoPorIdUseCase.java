package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.shared.application.logging.Logger;

public class BuscarVeiculoPorIdUseCase {

    private final BuscarVeiculoPorIdGateway gateway;
    private final Logger logger;

    public BuscarVeiculoPorIdUseCase(BuscarVeiculoPorIdGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public Veiculo execute(Long id) {
        logger.logInfo("Buscando veículo por id - veiculoId={}", id);
        return gateway.getVeiculoPorId(id);
    }
}
