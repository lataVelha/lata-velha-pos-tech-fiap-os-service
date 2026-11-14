package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.events.PaymentDoneEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceberPaymentDoneUseCaseTest {

    private static final Long OS_ID = 1L;

    @Mock
    private ReceberPaymentDoneGateway gateway;

    @Mock
    private NotificarOrdemServicoService notificarService;

    @Mock
    private Logger logger;

    private ReceberPaymentDoneUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReceberPaymentDoneUseCase(gateway, notificarService, logger);
    }

    private OrdemServico os(StatusOrdemServico status) {
        var execucao = ExecucaoServico.create(1L, OS_ID, new BigDecimal("150.00"));
        execucao.setStatus(StatusExecucaoServico.FINALIZADO);
        return new OrdemServico(
                OS_ID, 3L, 4L, "Barulho ao frear",
                status,
                LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now(), null, null,
                2L, 10L, new ArrayList<>(List.of(execucao)));
    }

    @Test
    @DisplayName("deve marcar OS como PAGA quando está FINALIZADA")
    void deveMarcarComoPaga() {
        var ordemServico = os(StatusOrdemServico.FINALIZADA);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(ordemServico);
        when(gateway.salvarOrdemServico(ordemServico)).thenReturn(ordemServico);

        useCase.execute(new PaymentDoneEvent(OS_ID));

        assertThat(ordemServico.getStatus()).isEqualTo(StatusOrdemServico.PAGA);
        verify(gateway).salvarOrdemServico(ordemServico);
        verify(notificarService).execute(ordemServico);
    }

    @Test
    @DisplayName("deve tratar PaymentDone repetido como no-op quando OS já está PAGA")
    void deveSerIdempotente() {
        var ordemServico = os(StatusOrdemServico.PAGA);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(ordemServico);

        useCase.execute(new PaymentDoneEvent(OS_ID));

        assertThat(ordemServico.getStatus()).isEqualTo(StatusOrdemServico.PAGA);
        verify(gateway, never()).salvarOrdemServico(any());
        verify(notificarService, never()).execute(any());
    }

    @Test
    @DisplayName("não deve alterar OS em status inesperado")
    void naoDeveAlterarStatusInesperado() {
        var ordemServico = os(StatusOrdemServico.EM_EXECUCAO);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(ordemServico);

        useCase.execute(new PaymentDoneEvent(OS_ID));

        assertThat(ordemServico.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
        verify(gateway, never()).salvarOrdemServico(any());
        verify(notificarService, never()).execute(any());
    }

    @Test
    @DisplayName("OS paga por este fluxo deve poder ser entregue")
    void osPagaDeveSerEntregavel() {
        var ordemServico = os(StatusOrdemServico.FINALIZADA);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(ordemServico);
        when(gateway.salvarOrdemServico(ordemServico)).thenReturn(ordemServico);

        useCase.execute(new PaymentDoneEvent(OS_ID));

        assertThat(ordemServico.podeSerEntregue()).isTrue();
    }
}
