package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.shared.application.logging.Logger;

public class DesativarVeiculoUseCase {

    private final DesativarVeiculoGateway gateway;
    private final Logger logger;

    public DesativarVeiculoUseCase(DesativarVeiculoGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public void execute(Long id) {
        logger.logInfo("Buscando veículo para desativação - veiculoId={}", id);
        var veiculo = gateway.getVeiculoPorId(id);
        veiculo.deactivate();

        logger.logInfo("Salvando desativação do veículo - veiculoId={}", id);
        gateway.salvarVeiculo(veiculo);
        logger.logInfo("Veículo desativado com sucesso - veiculoId={}", id);
    }
}
