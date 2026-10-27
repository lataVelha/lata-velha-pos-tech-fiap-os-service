package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.events.CobrancaEmitidaEvent;
import br.com.lata.velha.shared.application.logging.Logger;

public class RegistrarCobrancaEmitidaUseCase {

    private final Logger logger;

    public RegistrarCobrancaEmitidaUseCase(Logger logger) {
        this.logger = logger;
    }

    public void execute(CobrancaEmitidaEvent event) {
        logger.logInfo("Cobrança emitida recebida do BillingService - osId={}", event.getOsId());
    }
}
