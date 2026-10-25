package br.com.lata.velha.ordem_servico.application.event_handlers;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.RegistrarCobrancaEmitidaUseCase;
import br.com.lata.velha.ordem_servico.domain.events.CobrancaEmitidaEvent;
import br.com.lata.velha.shared.application.gateways.EventHandler;

public class CobrancaEmitidaEventHandler implements EventHandler<CobrancaEmitidaEvent> {

    private final RegistrarCobrancaEmitidaUseCase useCase;

    public CobrancaEmitidaEventHandler(RegistrarCobrancaEmitidaUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public String eventType() {
        return "CobrancaEmitida";
    }

    @Override
    public Class<CobrancaEmitidaEvent> eventClass() {
        return CobrancaEmitidaEvent.class;
    }

    @Override
    public void handle(CobrancaEmitidaEvent event) {
        useCase.execute(event);
    }
}
