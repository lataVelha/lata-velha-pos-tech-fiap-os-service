package br.com.lata.velha.ordem_servico.domain.events;

import br.com.lata.velha.shared.domain.events.AbstractDomainEvent;

public final class OrcamentoAprovadoEvent extends AbstractDomainEvent {

    private final Long osId;
    private final java.math.BigDecimal valorTotal;

    public OrcamentoAprovadoEvent(Long osId, java.math.BigDecimal valorTotal) {
        this.osId = osId;
        this.valorTotal = valorTotal;
    }

    @Override
    public String eventType() {
        return "OrcamentoAprovado";
    }

    public Long getOsId() {
        return osId;
    }

    public java.math.BigDecimal getValorTotal() {
        return valorTotal;
    }
}
