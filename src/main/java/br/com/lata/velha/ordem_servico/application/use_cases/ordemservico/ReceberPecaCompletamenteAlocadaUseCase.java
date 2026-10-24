package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.events.PecaCompletamenteAlocadaEvent;
import br.com.lata.velha.shared.application.logging.Logger;

public class ReceberPecaCompletamenteAlocadaUseCase {

    private final ReceberPecaCompletamenteAlocadaGateway gateway;
    private final Logger logger;

    public ReceberPecaCompletamenteAlocadaUseCase(ReceberPecaCompletamenteAlocadaGateway gateway, Logger logger) {
        this.gateway = gateway;
        this.logger = logger;
    }

    public void execute(PecaCompletamenteAlocadaEvent event) {
        logger.logInfo("Processando PecaCompletamenteAlocada - execucaoServicoId={}", event.getExecucaoServicoId());
        var execucaoLookup = gateway.getExecucaoServicoPorId(event.getExecucaoServicoId());
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(execucaoLookup.getOrdemServicoId());
        var execucao = ordemServico.getExecucaoById(event.getExecucaoServicoId());

        if (execucao.isAguardandoPeca()) {
            execucao.confirmarPecaDisponivel();
            gateway.salvarOrdemServico(ordemServico);
            logger.logInfo("Serviço confirmado como aprovado após peça completamente alocada - execucaoServicoId={}", event.getExecucaoServicoId());
        } else if (execucao.isAprovado()) {
            logger.logInfo("PecaCompletamenteAlocada já processado anteriormente - execucaoServicoId={}", event.getExecucaoServicoId());
        } else {
            logger.logWarn("PecaCompletamenteAlocada recebido para serviço em status inesperado - execucaoServicoId={}, status={}", event.getExecucaoServicoId(), execucao.getStatus());
        }
    }
}
