package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.shared.application.logging.Logger;

public class ReativarVeiculoUseCase {

    private final ReativarVeiculoGateway gateway;
    private final Logger logger;

    public ReativarVeiculoUseCase(ReativarVeiculoGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public Veiculo execute(Long id) {
        logger.logInfo("Buscando veículo inativo para reativação - veiculoId={}", id);
        Veiculo veiculo = gateway.getVeiculoInativoPorId(id);
        veiculo.activate();

        logger.logInfo("Salvando reativação do veículo - veiculoId={}", id);
        var reativado = gateway.salvarVeiculo(veiculo);
        logger.logInfo("Veículo reativado com sucesso - veiculoId={}", id);
        return reativado;
    }
}
