package br.com.lata.velha.ordem_servico.application.event_handlers;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberPecaCompletamenteAlocadaUseCase;
import br.com.lata.velha.ordem_servico.domain.events.PecaCompletamenteAlocadaEvent;
import br.com.lata.velha.shared.application.gateways.EventHandler;

public class PecaCompletamenteAlocadaEventHandler implements EventHandler<PecaCompletamenteAlocadaEvent> {

    private final ReceberPecaCompletamenteAlocadaUseCase useCase;

    public PecaCompletamenteAlocadaEventHandler(ReceberPecaCompletamenteAlocadaUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public String eventType() {
        return "PecaCompletamenteAlocada";
    }

    @Override
    public Class<PecaCompletamenteAlocadaEvent> eventClass() {
        return PecaCompletamenteAlocadaEvent.class;
    }

    @Override
    public void handle(PecaCompletamenteAlocadaEvent event) {
        useCase.execute(event);
    }
}
