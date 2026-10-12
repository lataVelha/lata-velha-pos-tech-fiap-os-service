package br.com.lata.velha.ordem_servico.application.use_cases.veiculo;

import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.shared.application.logging.Logger;

import java.util.List;

public class ListarVeiculosPorProprietarioUseCase {

    private final ListarVeiculosPorProprietarioGateway gateway;
    private final Logger logger;

    public ListarVeiculosPorProprietarioUseCase(ListarVeiculosPorProprietarioGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public List<Veiculo> execute(Long proprietarioId) {
        logger.logInfo("Listando veículos por proprietário - proprietarioId={}", proprietarioId);
        return gateway.findByProprietarioId(proprietarioId);
    }
}
