package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.events.InventoryCheckedEvent;
import br.com.lata.velha.shared.application.logging.Logger;

public class ReceberInventoryCheckedUseCase {

    private final ReceberInventoryCheckedGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public ReceberInventoryCheckedUseCase(ReceberInventoryCheckedGateway gateway,
                                          NotificarOrdemServicoService notificarService,
                                          Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public void execute(InventoryCheckedEvent event) {
        logger.logInfo("Processando InventoryChecked - osId={}", event.getOsId());
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(event.getOsId());

        for (var item : event.getItens()) {
            if (Boolean.FALSE.equals(item.pecasDisponiveis())) {
                var execucao = ordemServico.getExecucaoById(item.execucaoServicoId());
                if (execucao.isAprovado()) {
                    execucao.marcarAguardandoPeca();
                }
            }
        }

        if (ordemServico.isAprovada()) {
            ordemServico.iniciarExecucao();
        }

        var saved = gateway.salvarOrdemServico(ordemServico);
        notificarService.execute(saved);
        logger.logInfo("InventoryChecked processado - osId={}, status={}", saved.getId(), saved.getStatus());
    }
}
