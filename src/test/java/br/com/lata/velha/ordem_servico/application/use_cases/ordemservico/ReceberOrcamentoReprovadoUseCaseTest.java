package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.events.OrcamentoReprovadoEvent;
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
class ReceberOrcamentoReprovadoUseCaseTest {

    private static final Long OS_ID = 1L;

    @Mock
    private ReceberOrcamentoReprovadoGateway gateway;

    @Mock
    private NotificarOrdemServicoService notificarService;

    @Mock
    private Logger logger;

    private ReceberOrcamentoReprovadoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReceberOrcamentoReprovadoUseCase(gateway, notificarService, logger);
    }

    private OrdemServico os(StatusOrdemServico status) {
        var execucao = new ExecucaoServico(10L, 1L, OS_ID, StatusExecucaoServico.PENDENTE,
                new BigDecimal("150.00"), new java.util.HashSet<>(), null, null, null, null, LocalDateTime.now());
        return new OrdemServico(
                OS_ID, 3L, 4L, "Barulho ao frear",
                status,
                LocalDateTime.now(), null, null, null, null,
                2L, 10L, new ArrayList<>(List.of(execucao)));
    }

    @Test
    @DisplayName("deve reprovar a OS quando está AGUARDANDO_APROVACAO")
    void deveReprovarOs() {
        var ordemServico = os(StatusOrdemServico.AGUARDANDO_APROVACAO);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(ordemServico);
        when(gateway.salvarOrdemServico(ordemServico)).thenReturn(ordemServico);

        useCase.execute(new OrcamentoReprovadoEvent(OS_ID));

        assertThat(ordemServico.getStatus()).isEqualTo(StatusOrdemServico.REPROVADA);
        verify(notificarService).execute(ordemServico);
    }

    @Test
    @DisplayName("OS reprovada deve poder ser entregue, pois nada é devido")
    void osReprovadaDeveSerEntregavel() {
        var ordemServico = os(StatusOrdemServico.AGUARDANDO_APROVACAO);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(ordemServico);
        when(gateway.salvarOrdemServico(ordemServico)).thenReturn(ordemServico);

        useCase.execute(new OrcamentoReprovadoEvent(OS_ID));

        assertThat(ordemServico.podeSerEntregue()).isTrue();
    }

    @Test
    @DisplayName("deve tratar OrcamentoReprovado repetido como no-op")
    void deveSerIdempotente() {
        var ordemServico = os(StatusOrdemServico.REPROVADA);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(ordemServico);

        useCase.execute(new OrcamentoReprovadoEvent(OS_ID));

        assertThat(ordemServico.getStatus()).isEqualTo(StatusOrdemServico.REPROVADA);
        verify(gateway, never()).salvarOrdemServico(any());
        verify(notificarService, never()).execute(any());
    }

    @Test
    @DisplayName("não deve alterar OS em status inesperado")
    void naoDeveAlterarStatusInesperado() {
        var ordemServico = os(StatusOrdemServico.EM_EXECUCAO);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(ordemServico);

        useCase.execute(new OrcamentoReprovadoEvent(OS_ID));

        assertThat(ordemServico.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
        verify(gateway, never()).salvarOrdemServico(any());
    }
}
