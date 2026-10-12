package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.dtos.request.AtualizarServicoRequest;
import br.com.lata.velha.ordem_servico.domain.entities.Servico;
import br.com.lata.velha.shared.application.logging.Logger;

public class AtualizarServicoUseCase {

    private final AtualizarServicoGateway gateway;
    private final Logger logger;

    public AtualizarServicoUseCase(AtualizarServicoGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public Servico execute(Long id, AtualizarServicoRequest request) {
        logger.logInfo("Buscando serviço para atualização - servicoId={}", id);
        Servico servico = gateway.getServicoPorId(id);
        servico.atualizar(request.nome(), request.descricao());

        logger.logInfo("Salvando serviço atualizado - servicoId={}", id);
        var updated = gateway.salvarServico(servico);
        logger.logInfo("Serviço atualizado com sucesso - servicoId={}", id);
        return updated;
    }
}
