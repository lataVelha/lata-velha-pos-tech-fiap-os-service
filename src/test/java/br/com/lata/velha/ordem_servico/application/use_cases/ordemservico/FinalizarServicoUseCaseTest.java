package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.services.ordemservico.NotificarOrdemServicoService;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.entities.Funcionario;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.shared.application.logging.Logger;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinalizarServicoUseCaseTest {

    @Mock private FinalizarServicoGateway gateway;
    @Mock private NotificarOrdemServicoService notificarService;
    @Mock private Logger logger;

    private FinalizarServicoUseCase useCase;

    private static final Long OS_ID = 1L;
    private static final Long MECANICO_ID = 2L;
    private static final Long EXEC_ID = 10L;

    private Funcionario mecanico;
    private UserId userId;

    @BeforeEach
    void setUp() {
        useCase = new FinalizarServicoUseCase(gateway, notificarService, logger);
        mecanico = new Funcionario(MECANICO_ID, "Carlos Mecânico", null, null);
        userId = UserId.random();
    }

    private ExecucaoServico buildExecEmExecucao(Long id) {
        return new ExecucaoServico(id, 99L, OS_ID, StatusExecucaoServico.EM_EXECUCAO,
                new BigDecimal("150"), new HashSet<>(), 1L, MECANICO_ID,
                LocalDateTime.now(), null, LocalDateTime.now());
    }

    private ExecucaoServico buildExecAprovado(Long id) {
        return new ExecucaoServico(id, 88L, OS_ID, StatusExecucaoServico.APROVADO,
                new BigDecimal("100"), new HashSet<>(), 1L, null,
                null, null, LocalDateTime.now());
    }

    private OrdemServico buildOsEmExecucao(List<ExecucaoServico> execucoes) {
        return new OrdemServico(OS_ID, 1L, 2L, "Barulho ao frear",
                StatusOrdemServico.EM_EXECUCAO, LocalDateTime.now(), LocalDateTime.now(),
                null, null, null, 3L, MECANICO_ID, new ArrayList<>(execucoes));
    }

    @Test
    @DisplayName("deve finalizar serviço e notificar quando OS é finalizada")
    void deveFinalizarServicoENotificarQuandoOsEhFinalizada() {
        var exec = buildExecEmExecucao(EXEC_ID);
        var os = buildOsEmExecucao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId));

        assertThat(exec.getStatus()).isEqualTo(StatusExecucaoServico.FINALIZADO);
        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.FINALIZADA);
        verify(notificarService).execute(os);
    }

    @Test
    @DisplayName("deve finalizar serviço sem notificar quando existem outros serviços pendentes")
    void deveFinalizarServicoSemNotificarQuandoOutrosServicosExistem() {
        var exec1 = buildExecEmExecucao(EXEC_ID);
        var exec2 = buildExecAprovado(11L);
        var os = buildOsEmExecucao(List.of(exec1, exec2));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId));

        assertThat(exec1.getStatus()).isEqualTo(StatusExecucaoServico.FINALIZADO);
        assertThat(os.getStatus()).isEqualTo(StatusOrdemServico.EM_EXECUCAO);
        verify(notificarService, never()).execute(any());
    }

    @Test
    @DisplayName("deve salvar OS após finalizar serviço")
    void deveSalvarOsAposFinalizarServico() {
        var exec = buildExecEmExecucao(EXEC_ID);
        var os = buildOsEmExecucao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId));

        verify(gateway).salvarOrdemServico(os);
    }

    @Test
    @DisplayName("deve propagar exceção quando OS não encontrada")
    void devePropagateExcecaoQuandoOsNaoEncontrada() {
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID))
                .thenThrow(new RuntimeException("OS não encontrada"));

        var input = new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId);
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("OS não encontrada");

        verify(gateway, never()).salvarOrdemServico(any());
        verify(notificarService, never()).execute(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando mecânico não encontrado")
    void devePropagateExcecaoQuandoMecanicoNaoEncontrado() {
        when(gateway.getOrdemServicoComServicosEPecas(OS_ID))
                .thenReturn(buildOsEmExecucao(List.of(buildExecEmExecucao(EXEC_ID))));
        when(gateway.getFuncionarioPorUserId(userId)).thenThrow(new RuntimeException("Mecânico não encontrado"));

        var input = new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId);
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Mecânico não encontrado");

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve propagar exceção quando serviço não pertence à OS")
    void devePropagateExcecaoQuandoServicoNaoPertenceAOs() {
        var exec = buildExecEmExecucao(EXEC_ID);
        var os = buildOsEmExecucao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);

        Long execIdInvalido = 999L;
        var input = new FinalizarServicoUseCase.Input(OS_ID, execIdInvalido, userId);
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(String.valueOf(execIdInvalido));

        verify(gateway, never()).salvarOrdemServico(any());
    }

    @Test
    @DisplayName("deve atribuir mecânico responsável ao finalizar última execução")
    void deveAtribuirMecanicoResponsavelAoFinalizar() {
        var exec = buildExecEmExecucao(EXEC_ID);
        var os = buildOsEmExecucao(List.of(exec));

        when(gateway.getOrdemServicoComServicosEPecas(OS_ID)).thenReturn(os);
        when(gateway.getFuncionarioPorUserId(userId)).thenReturn(mecanico);
        when(gateway.salvarOrdemServico(any())).thenReturn(os);

        useCase.execute(new FinalizarServicoUseCase.Input(OS_ID, EXEC_ID, userId));

        assertThat(os.getMecanicoResponsavelId()).isEqualTo(MECANICO_ID);
    }
}
