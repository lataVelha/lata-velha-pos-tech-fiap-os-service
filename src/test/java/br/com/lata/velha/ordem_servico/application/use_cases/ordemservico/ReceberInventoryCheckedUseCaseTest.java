package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.events.InventoryCheckedEvent;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceberInventoryCheckedUseCaseTest {

    private static final Long OS_ID = 1L;
    private static final Long EXEC_A = 10L;
    private static final Long EXEC_B = 11L;

    @Mock
    private ReceberInventoryCheckedGateway gateway;

    @Mock
    private NotificarOrdemServicoService notificarService;

    @Mock
    private Logger logger;

    private ReceberInventoryCheckedUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReceberInventoryCheckedUseCase(gateway, notificarService, logger);
    }

    private ExecucaoServico execucaoAprovada(Long id) {
        return new ExecucaoServico(id, id, OS_ID, StatusExecucaoServico.APROVADO,
                new BigDecimal("150.00"), new java.util.HashSet<>(), 2L, null, null, null, LocalDateTime.now());
    }

    private OrdemServico osAprovada(ExecucaoServico... execucoes) {
        return new OrdemServico(
                OS_ID, 3L, 4L, "Barulho ao frear",
                StatusOrdemServico.APROVADA,
                LocalDateTime.now(), null, null, null, null,
                2L, 10L, new ArrayList<>(List.of(execucoes)));
    }

    @Test
    @DisplayName("deve iniciar a execução da OS quando todas as peças estão disponíveis")
    void deveIniciarExecucaoQuandoTudoDisponivel() {
        var execucao = execucaoAprovada(EXEC_A);
        var ordemServico = osAprovada(execucao);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(ordemServico);
        when(gateway.salvarOrdemServico(ordemServico)).thenReturn(ordemServico);

        useCase.execute(new InventoryCheckedEvent(OS_ID,
                List.of(new InventoryCheckedEvent.ItemDisponibilidade(EXEC_A, true))));

        assertThat(ordemServico.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
        assertThat(execucao.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
        verify(notificarService).execute(ordemServico);
    }

    @Test
    @DisplayName("deve marcar AGUARDANDO_PECA apenas na execução sem peças disponíveis")
    void deveMarcarApenasExecucaoBloqueada() {
        var execucaoOk = execucaoAprovada(EXEC_A);
        var execucaoBloqueada = execucaoAprovada(EXEC_B);
        var ordemServico = osAprovada(execucaoOk, execucaoBloqueada);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(ordemServico);
        when(gateway.salvarOrdemServico(ordemServico)).thenReturn(ordemServico);

        useCase.execute(new InventoryCheckedEvent(OS_ID, List.of(
                new InventoryCheckedEvent.ItemDisponibilidade(EXEC_A, true),
                new InventoryCheckedEvent.ItemDisponibilidade(EXEC_B, false))));

        assertThat(execucaoOk.getStatus()).isEqualTo(StatusExecucaoServico.APROVADO);
        assertThat(execucaoBloqueada.getStatus()).isEqualTo(StatusExecucaoServico.AGUARDANDO_PECA);
    }

    @Test
    @DisplayName("OS entra em EM_EXECUCAO mesmo quando toda execução aguarda peça")
    void osEntraEmExecucaoMesmoBloqueada() {
        var execucao = execucaoAprovada(EXEC_A);
        var ordemServico = osAprovada(execucao);
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(ordemServico);
        when(gateway.salvarOrdemServico(ordemServico)).thenReturn(ordemServico);

        useCase.execute(new InventoryCheckedEvent(OS_ID,
                List.of(new InventoryCheckedEvent.ItemDisponibilidade(EXEC_A, false))));

        assertThat(execucao.getStatus()).isEqualTo(StatusExecucaoServico.AGUARDANDO_PECA);
        assertThat(ordemServico.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
    }

    @Test
    @DisplayName("não deve reiniciar execução quando a OS já está EM_EXECUCAO")
    void naoDeveReiniciarExecucao() {
        var execucao = execucaoAprovada(EXEC_A);
        var ordemServico = new OrdemServico(
                OS_ID, 3L, 4L, "Barulho ao frear",
                StatusOrdemServico.EM_EXECUCAO,
                LocalDateTime.now(), LocalDateTime.now(), null, null, null,
                2L, 10L, new ArrayList<>(List.of(execucao)));
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(ordemServico);
        when(gateway.salvarOrdemServico(ordemServico)).thenReturn(ordemServico);

        useCase.execute(new InventoryCheckedEvent(OS_ID,
                List.of(new InventoryCheckedEvent.ItemDisponibilidade(EXEC_A, true))));

        assertThat(ordemServico.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
    }
}
