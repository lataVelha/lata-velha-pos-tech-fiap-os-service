package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.application.dtos.request.VeiculoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.shared.application.logging.Logger;

public class CriarVeiculoUseCase {

    private final CriarVeiculoGateway gateway;
    private final Logger logger;

    public CriarVeiculoUseCase(CriarVeiculoGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public Veiculo execute(VeiculoRequest request) {
        logger.logInfo("Validando proprietário do veículo - proprietarioId={}, placa={}", request.proprietarioId(), request.placa());
        gateway.getProprietarioAtivoPorId(request.proprietarioId());

        logger.logInfo("Salvando veículo - proprietarioId={}, placa={}", request.proprietarioId(), request.placa());
        var saved = gateway.salvarVeiculo(request.toDomain());
        logger.logInfo("Veículo criado com sucesso - veiculoId={}", saved.getId());
        return saved;
    }
}
