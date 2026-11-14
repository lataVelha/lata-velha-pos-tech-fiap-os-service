package br.com.lata.velha.ordem_servico.application.event_handlers;

import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberInventoryCheckedUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberOrcamentoAprovadoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberOrcamentoReprovadoUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberPaymentDoneUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.ReceberPecaCompletamenteAlocadaUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.RegistrarCobrancaEmitidaUseCase;
import br.com.lata.velha.ordem_servico.application.use_cases.ordemservico.RegistrarFalhaDeducaoEstoqueUseCase;
import br.com.lata.velha.shared.domain.events.CobrancaEmitidaEvent;
import br.com.lata.velha.shared.domain.events.DeducaoEstoqueFalhouEvent;
import br.com.lata.velha.shared.domain.events.InventoryCheckedEvent;
import br.com.lata.velha.shared.domain.events.OrcamentoAprovadoEvent;
import br.com.lata.velha.shared.domain.events.OrcamentoReprovadoEvent;
import br.com.lata.velha.shared.domain.events.PaymentDoneEvent;
import br.com.lata.velha.shared.domain.events.PecaCompletamenteAlocadaEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventHandlersTest {

    @Mock
    private ReceberOrcamentoAprovadoUseCase receberOrcamentoAprovadoUseCase;

    @Mock
    private ReceberOrcamentoReprovadoUseCase receberOrcamentoReprovadoUseCase;

    @Mock
    private ReceberInventoryCheckedUseCase receberInventoryCheckedUseCase;

    @Mock
    private ReceberPecaCompletamenteAlocadaUseCase receberPecaCompletamenteAlocadaUseCase;

    @Mock
    private RegistrarFalhaDeducaoEstoqueUseCase registrarFalhaDeducaoEstoqueUseCase;

    @Mock
    private RegistrarCobrancaEmitidaUseCase registrarCobrancaEmitidaUseCase;

    @Mock
    private ReceberPaymentDoneUseCase receberPaymentDoneUseCase;

    @Test
    @DisplayName("OrcamentoAprovadoEventHandler deve identificar e delegar o evento")
    void deveTratarOrcamentoAprovado() {
        var handler = new OrcamentoAprovadoEventHandler(receberOrcamentoAprovadoUseCase);
        var event = new OrcamentoAprovadoEvent(1L, null, List.of());

        handler.handle(event);

        assertThat(handler.eventType()).isEqualTo("OrcamentoAprovado");
        assertThat(handler.eventClass()).isEqualTo(OrcamentoAprovadoEvent.class);
        verify(receberOrcamentoAprovadoUseCase).execute(event);
    }

    @Test
    @DisplayName("OrcamentoReprovadoEventHandler deve identificar e delegar o evento")
    void deveTratarOrcamentoReprovado() {
        var handler = new OrcamentoReprovadoEventHandler(receberOrcamentoReprovadoUseCase);
        var event = new OrcamentoReprovadoEvent(1L);

        handler.handle(event);

        assertThat(handler.eventType()).isEqualTo("OrcamentoReprovado");
        assertThat(handler.eventClass()).isEqualTo(OrcamentoReprovadoEvent.class);
        verify(receberOrcamentoReprovadoUseCase).execute(event);
    }

    @Test
    @DisplayName("InventoryCheckedEventHandler deve identificar e delegar o evento")
    void deveTratarInventoryChecked() {
        var handler = new InventoryCheckedEventHandler(receberInventoryCheckedUseCase);
        var event = new InventoryCheckedEvent(1L, List.of());

        handler.handle(event);

        assertThat(handler.eventType()).isEqualTo("InventoryChecked");
        assertThat(handler.eventClass()).isEqualTo(InventoryCheckedEvent.class);
        verify(receberInventoryCheckedUseCase).execute(event);
    }

    @Test
    @DisplayName("PecaCompletamenteAlocadaEventHandler deve identificar e delegar o evento")
    void deveTratarPecaCompletamenteAlocada() {
        var handler = new PecaCompletamenteAlocadaEventHandler(receberPecaCompletamenteAlocadaUseCase);
        var event = new PecaCompletamenteAlocadaEvent(10L);

        handler.handle(event);

        assertThat(handler.eventType()).isEqualTo("PecaCompletamenteAlocada");
        assertThat(handler.eventClass()).isEqualTo(PecaCompletamenteAlocadaEvent.class);
        verify(receberPecaCompletamenteAlocadaUseCase).execute(event);
    }

    @Test
    @DisplayName("DeducaoEstoqueFalhouEventHandler deve identificar e delegar o evento")
    void deveTratarDeducaoEstoqueFalhou() {
        var handler = new DeducaoEstoqueFalhouEventHandler(registrarFalhaDeducaoEstoqueUseCase);
        var event = new DeducaoEstoqueFalhouEvent(1L, 10L, "estoque insuficiente");

        handler.handle(event);

        assertThat(handler.eventType()).isEqualTo("DeducaoEstoqueFalhou");
        assertThat(handler.eventClass()).isEqualTo(DeducaoEstoqueFalhouEvent.class);
        verify(registrarFalhaDeducaoEstoqueUseCase).execute(event);
    }

    @Test
    @DisplayName("CobrancaEmitidaEventHandler deve identificar e delegar o evento")
    void deveTratarCobrancaEmitida() {
        var handler = new CobrancaEmitidaEventHandler(registrarCobrancaEmitidaUseCase);
        var event = new CobrancaEmitidaEvent(1L);

        handler.handle(event);

        assertThat(handler.eventType()).isEqualTo("CobrancaEmitida");
        assertThat(handler.eventClass()).isEqualTo(CobrancaEmitidaEvent.class);
        verify(registrarCobrancaEmitidaUseCase).execute(event);
    }

    @Test
    @DisplayName("PaymentDoneEventHandler deve identificar e delegar o evento")
    void deveTratarPaymentDone() {
        var handler = new PaymentDoneEventHandler(receberPaymentDoneUseCase);
        var event = new PaymentDoneEvent(1L);

        handler.handle(event);

        assertThat(handler.eventType()).isEqualTo("PaymentDone");
        assertThat(handler.eventClass()).isEqualTo(PaymentDoneEvent.class);
        verify(receberPaymentDoneUseCase).execute(event);
    }
}
