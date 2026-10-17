package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.shared.application.logging.Logger;

public class AtualizarVeiculoUseCase {

    private final AtualizarVeiculoGateway gateway;
    private final Logger logger;

    public AtualizarVeiculoUseCase(AtualizarVeiculoGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public Veiculo execute(Long id, VeiculoRequest request) {
        logger.logInfo("Buscando veículo para atualização - veiculoId={}", id);
        Veiculo existing = gateway.getVeiculoPorId(id);

        logger.logInfo("Validando proprietário do veículo - veiculoId={}, proprietarioId={}", id, request.proprietarioId());
        gateway.getProprietarioAtivoPorId(request.proprietarioId());
        request.updateDomain(existing);

        logger.logInfo("Salvando veículo atualizado - veiculoId={}", id);
        var updated = gateway.salvarVeiculo(existing);
        logger.logInfo("Veículo atualizado com sucesso - veiculoId={}", id);
        return updated;
    }
}
