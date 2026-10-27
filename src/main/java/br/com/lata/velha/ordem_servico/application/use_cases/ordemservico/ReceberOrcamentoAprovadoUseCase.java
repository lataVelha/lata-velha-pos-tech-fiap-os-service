package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.events.OrcamentoAprovadoEvent;
import br.com.lata.velha.shared.application.logging.Logger;

public class ReceberOrcamentoAprovadoUseCase {

    private final ReceberOrcamentoAprovadoGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public ReceberOrcamentoAprovadoUseCase(ReceberOrcamentoAprovadoGateway gateway,
                                           NotificarOrdemServicoService notificarService,
                                           Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public void execute(OrcamentoAprovadoEvent event) {
        logger.logInfo("Processando OrcamentoAprovado - osId={}, valorTotal={}", event.getOsId(), event.getValorTotal());
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(event.getOsId());

        if (ordemServico.getStatus() == StatusOrdemServico.EM_EXECUCAO) {
            logger.logInfo("OrcamentoAprovado já processado anteriormente - osId={}", event.getOsId());
            return;
        }

        if (ordemServico.getStatus() != StatusOrdemServico.AGUARDANDO_APROVACAO) {
            logger.logWarn("OrcamentoAprovado recebido para OS em status inesperado - osId={}, status={}",
                    event.getOsId(), ordemServico.getStatus());
            return;
        }

        ordemServico.getExecucaoServicos().forEach(execucao -> execucao.aprovar(null));
        ordemServico.aprovar(null);

        var saved = gateway.salvarOrdemServico(ordemServico);
        notificarService.execute(saved);
        logger.logInfo("OS aprovada e em execução - osId={}, status={}", saved.getId(), saved.getStatus());
    }
}
