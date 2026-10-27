package br.com.lata.velha.ordem_servico.application.event_handlers;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberPaymentDoneUseCase;
import br.com.lata.velha.ordem_servico.domain.events.PaymentDoneEvent;
import br.com.lata.velha.shared.application.gateways.EventHandler;

public class PaymentDoneEventHandler implements EventHandler<PaymentDoneEvent> {

    private final ReceberPaymentDoneUseCase useCase;

    public PaymentDoneEventHandler(ReceberPaymentDoneUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public String eventType() {
        return "PaymentDone";
    }

    @Override
    public Class<PaymentDoneEvent> eventClass() {
        return PaymentDoneEvent.class;
    }

    @Override
    public void handle(PaymentDoneEvent event) {
        useCase.execute(event);
    }
}
