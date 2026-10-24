package br.com.lata.velha.ordem_servico.application.event_handlers;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberInventoryCheckedUseCase;
import br.com.lata.velha.ordem_servico.domain.events.InventoryCheckedEvent;
import br.com.lata.velha.shared.application.gateways.EventHandler;

public class InventoryCheckedEventHandler implements EventHandler<InventoryCheckedEvent> {

    private final ReceberInventoryCheckedUseCase useCase;

    public InventoryCheckedEventHandler(ReceberInventoryCheckedUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public String eventType() {
        return "InventoryChecked";
    }

    @Override
    public Class<InventoryCheckedEvent> eventClass() {
        return InventoryCheckedEvent.class;
    }

    @Override
    public void handle(InventoryCheckedEvent event) {
        useCase.execute(event);
    }
}
