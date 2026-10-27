package br.com.lata.velha.ordem_servico.application.event_handlers;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberOrcamentoReprovadoUseCase;
import br.com.lata.velha.ordem_servico.domain.events.OrcamentoReprovadoEvent;
import br.com.lata.velha.shared.application.gateways.EventHandler;

public class OrcamentoReprovadoEventHandler implements EventHandler<OrcamentoReprovadoEvent> {

    private final ReceberOrcamentoReprovadoUseCase useCase;

    public OrcamentoReprovadoEventHandler(ReceberOrcamentoReprovadoUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public String eventType() {
        return "OrcamentoReprovado";
    }

    @Override
    public Class<OrcamentoReprovadoEvent> eventClass() {
        return OrcamentoReprovadoEvent.class;
    }

    @Override
    public void handle(OrcamentoReprovadoEvent event) {
        useCase.execute(event);
    }
}
