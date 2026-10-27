package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.events.OrcamentoReprovadoEvent;
import br.com.lata.velha.shared.application.logging.Logger;

public class ReceberOrcamentoReprovadoUseCase {

    private final ReceberOrcamentoReprovadoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public ReceberOrcamentoReprovadoUseCase(ReceberOrcamentoReprovadoGateway gateway,
                                            NotificarOrdemServicoService notificarService,
                                            Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public void execute(OrcamentoReprovadoEvent event) {
        logger.logInfo("Processando OrcamentoReprovado - osId={}", event.getOsId());
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(event.getOsId());

        if (ordemServico.getStatus() == StatusOrdemServico.AGUARDANDO_APROVACAO) {
            ordemServico.reprovar(null);
            var saved = gateway.salvarOrdemServico(ordemServico);
            notificarService.execute(saved);
            logger.logInfo("OS reprovada por orçamento reprovado - osId={}", saved.getId());
        } else if (ordemServico.getStatus() == StatusOrdemServico.REPROVADA) {
            logger.logInfo("OrcamentoReprovado já processado anteriormente - osId={}", event.getOsId());
        } else {
            logger.logWarn("OrcamentoReprovado recebido para OS em status inesperado - osId={}, status={}", event.getOsId(), ordemServico.getStatus());
        }
    }
}
