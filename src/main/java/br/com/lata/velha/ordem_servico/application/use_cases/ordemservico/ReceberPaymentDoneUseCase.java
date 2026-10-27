package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.events.PaymentDoneEvent;
import br.com.lata.velha.shared.application.logging.Logger;

public class ReceberPaymentDoneUseCase {

    private final ReceberPaymentDoneGateway gateway;
    private final NotificarOrdemServicoService notificarService;
    private final Logger logger;

    public ReceberPaymentDoneUseCase(ReceberPaymentDoneGateway gateway,
                                     NotificarOrdemServicoService notificarService,
                                     Logger logger) {
        this.gateway = gateway;
        this.notificarService = notificarService;
        this.logger = logger;
    }

    public void execute(PaymentDoneEvent event) {
        logger.logInfo("Processando PaymentDone - osId={}", event.getOsId());
        var ordemServico = gateway.getOrdemServicoComServicosEPecas(event.getOsId());

        if (ordemServico.isFinalizada()) {
            ordemServico.pagar();
            var saved = gateway.salvarOrdemServico(ordemServico);
            notificarService.execute(saved);
            logger.logInfo("OS marcada como paga - osId={}", saved.getId());
        } else if (ordemServico.isPaga()) {
            logger.logInfo("PaymentDone já processado anteriormente - osId={}", event.getOsId());
        } else {
            logger.logWarn("PaymentDone recebido para OS em status inesperado - osId={}, status={}",
                    event.getOsId(), ordemServico.getStatus());
        }
    }
}
