package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.domain.events.DeducaoEstoqueFalhouEvent;
import br.com.lata.velha.shared.application.logging.Logger;

public class RegistrarFalhaDeducaoEstoqueUseCase {

    private final Logger logger;

    public RegistrarFalhaDeducaoEstoqueUseCase(Logger logger) {
        this.logger = logger;
    }

    public void execute(DeducaoEstoqueFalhouEvent event) {
        logger.logError("Falha na dedução de estoque ao finalizar serviço (ação manual necessária) - osId={}, execucaoServicoId={}, motivo={}",
                event.getOsId(), event.getExecucaoServicoId(), event.getMotivo());
    }
}
