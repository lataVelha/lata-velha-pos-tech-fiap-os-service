package br.com.lata.velha.ordem_servico.application.event_handlers;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.RegistrarFalhaDeducaoEstoqueUseCase;
import br.com.lata.velha.ordem_servico.domain.events.DeducaoEstoqueFalhouEvent;
import br.com.lata.velha.shared.application.gateways.EventHandler;

public class DeducaoEstoqueFalhouEventHandler implements EventHandler<DeducaoEstoqueFalhouEvent> {

    private final RegistrarFalhaDeducaoEstoqueUseCase useCase;

    public DeducaoEstoqueFalhouEventHandler(RegistrarFalhaDeducaoEstoqueUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public String eventType() {
        return "DeducaoEstoqueFalhou";
    }

    @Override
    public Class<DeducaoEstoqueFalhouEvent> eventClass() {
        return DeducaoEstoqueFalhouEvent.class;
    }

    @Override
    public void handle(DeducaoEstoqueFalhouEvent event) {
        useCase.execute(event);
    }
}
