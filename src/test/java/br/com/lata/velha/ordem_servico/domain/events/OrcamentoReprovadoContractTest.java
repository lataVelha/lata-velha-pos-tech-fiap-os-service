package br.com.lata.velha.ordem_servico.domain.events;

import br.com.lata.velha.shared.infrastructure.messaging.EventSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contrato de OrcamentoReprovado (Story 14.3 / 15.1).
 */
class OrcamentoReprovadoContractTest {

    private final EventSerializer serializer = new EventSerializer();

    @Test
    @DisplayName("OrcamentoReprovado publica eventType e osId")
    void devePublicarContrato() {
        var event = new OrcamentoReprovadoEvent(42L);

        String json = serializer.serialize(event);

        assertThat(json)
                .contains("\"eventType\":\"OrcamentoReprovado\"")
                .contains("\"osId\":42");
    }

    @Test
    @DisplayName("payload do próprio serviço faz roundtrip sem perda")
    void deveFazerRoundtrip() {
        var envelope = serializer.deserializeEnvelope(serializer.serialize(new OrcamentoReprovadoEvent(7L)));

        var event = serializer.deserializePayload(envelope, OrcamentoReprovadoEvent.class);

        assertThat(event.getOsId()).isEqualTo(7L);
        assertThat(event.eventType()).isEqualTo("OrcamentoReprovado");
    }
}
