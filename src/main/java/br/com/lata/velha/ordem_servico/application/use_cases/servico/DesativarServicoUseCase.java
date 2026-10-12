package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.shared.application.logging.Logger;

public class DesativarServicoUseCase {

    private final DesativarServicoGateway gateway;
    private final Logger logger;

    public DesativarServicoUseCase(DesativarServicoGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public void execute(Long id) {
        logger.logInfo("Buscando serviço para desativação - servicoId={}", id);
        var servico = gateway.getServicoPorId(id);
        servico.desativar();

        logger.logInfo("Salvando desativação do serviço - servicoId={}", id);
        gateway.salvarServico(servico);
        logger.logInfo("Serviço desativado com sucesso - servicoId={}", id);
    }
}
